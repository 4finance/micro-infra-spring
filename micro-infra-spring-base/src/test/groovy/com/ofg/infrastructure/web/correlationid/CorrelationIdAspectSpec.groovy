package com.ofg.infrastructure.web.correlationid
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

@ActiveProfiles('aspect')
@ContextConfiguration(classes = [BaseConfiguration, CorrelationIdAspectSpecConfiguration], loader = SpringApplicationContextLoader)
class CorrelationIdAspectSpec extends MicroserviceMvcWiremockSpec {

    def "should set correlationId on header via aspect"() {
        given:
            stubInteraction(get(urlMatching('.*')), aResponse().withStatus(200))
        when:
            sendRequestToAspectEndpoint()
        then:
            wireMock.verifyThat(getRequestedFor(urlMatching('.*')).withHeader(CORRELATION_ID_HEADER, matching(/^(?!\s*$).+/)))
    }

    private MvcResult sendRequestToAspectEndpoint() {
        mockMvc.perform(MockMvcRequestBuilders.get('/aspect').accept(MediaType.TEXT_PLAIN)).andReturn()
    }

    @RestController
    @TypeChecked
    @PackageScope
    static class AspectTestingController {

        private final ServiceRestClient serviceRestClient
        private final HttpMockServer httpMockServer

        AspectTestingController(ServiceRestClient serviceRestClient, HttpMockServer httpMockServer) {
            this.serviceRestClient = serviceRestClient
            this.httpMockServer = httpMockServer
        }

        @RequestMapping(value = "/aspect", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        String ping() {
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
