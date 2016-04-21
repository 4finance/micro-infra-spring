package com.ofg.infrastructure.web.correlationid

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.WebAsyncTask
import spock.lang.Shared
import spock.lang.Unroll

import java.util.concurrent.Callable

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
import static java.util.concurrent.TimeUnit.SECONDS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [BaseConfiguration, CorrelationIdAspectSpecConfiguration], loader = SpringApplicationContextLoader)
class CorrelationIdAspectSpec extends MicroserviceMvcWiremockSpec {

    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2182');

    def "should set correlationId on header via aspect in synchronous call"() {
        given:
            stubInteraction(get(urlMatching('.*')), aResponse().withStatus(200))
        when:
            mockMvc.perform(MockMvcRequestBuilders.get('/syncPing').accept(MediaType.TEXT_PLAIN)).andReturn()
        then:
            wireMock.verifyThat(getRequestedFor(urlMatching('.*')).withHeader(CORRELATION_ID_HEADER, matching(/^(?!\s*$).+/)))
    }

    def "should set correlationId on header via aspect in asynchronous call using #url"() {
        given:
            stubInteraction(get(urlMatching('.*')), aResponse().withStatus(200))
        when:
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_PLAIN))
                    .andExpect(request().asyncStarted())
                    .andReturn()
        and:
            mvcResult.getAsyncResult(SECONDS.toMillis(2))
        and:
            mockMvc.perform(asyncDispatch(mvcResult)).
                    andDo(print()).
                    andExpect(status().isOk())
        then:
            wireMock.verifyThat(getRequestedFor(urlMatching('.*')).withHeader(CORRELATION_ID_HEADER, matching(/^(?!\s*$).+/)))

        where:
            url << ['/callablePing', '/webAsyncTaskPing']
    }

    @CompileStatic
    @Configuration
    @Import(AspectTestingController)
    @EnableAsync
    static class CorrelationIdAspectSpecConfiguration {
    }

    @RestController
    @TypeChecked
    @PackageScope
    static class AspectTestingController {

        @Autowired private ServiceRestClient serviceRestClient
        @Autowired private HttpMockServer httpMockServer

        @RequestMapping(value = "/syncPing", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        String syncPing() {
            callWiremockAndReturnOk()
        }

        @RequestMapping(value = "/callablePing", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        Callable<String> asyncPing() {
            return {
                callWiremockAndReturnOk()
            }
        }

        @RequestMapping(value = "/webAsyncTaskPing", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        WebAsyncTask<String> webAsyncTaskPing() {
            return new WebAsyncTask<>(
                {
                    callWiremockAndReturnOk()
                }
            );
        };

        private String callWiremockAndReturnOk() {
            serviceRestClient.forExternalService()
                    .get()
                    .onUrl(new URI("http://localhost:${httpMockServer.port()}"))
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(String)
            return "OK"
        }
    }

}
