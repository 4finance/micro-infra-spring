package com.ofg.infrastructure.web.resttemplate.fluent

import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.correlationid.HeadersSettingFilter
import org.hamcrest.Matchers
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.cloud.sleuth.instrument.web.TraceFilter
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spock.lang.Shared

import java.util.concurrent.Executors

@ActiveProfiles(['stub', BasicProfiles.TEST])
@ContextConfiguration(classes = [BaseConfiguration, SampleController, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class ServiceRestClientWebIntegrationSpec extends MvcWiremockIntegrationSpec {

    private static final ServiceAlias COLLABORATOR_ALIAS = new ServiceAlias('foo-bar')
    private static final String NON_EXISTING_PATH = '/non/existing/path'

    @Shared
    @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2183');

    @Autowired
    ServiceRestClient serviceRestClient

    @Autowired ServiceResolver serviceResolver
    @Autowired TraceFilter traceFilter

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        mockMvcBuilder.addFilters(new HeadersSettingFilter(), traceFilter)
    }

    def "should pass correlationid when calling service via retry executor"() {
        expect:
            mockMvc.perform(MockMvcRequestBuilders.get('/response'))
                    .andExpect(MockMvcResultMatchers.header().string(CorrelationIdHolder.CORRELATION_ID_HEADER, Matchers.notNullValue()))
                    .andExpect(MockMvcResultMatchers.header().string(CorrelationIdHolder.OLD_CORRELATION_ID_HEADER, Matchers.notNullValue()))
    }

    @Configuration
    @RestController
    static class SampleController {

        @Autowired ServiceRestClient serviceRestClient

        @RequestMapping('/response')
        String response() {
            try {
                return serviceRestClient
                        .forService(COLLABORATOR_ALIAS)
                        .retryUsing(new AsyncRetryExecutor(Executors.newSingleThreadScheduledExecutor()).withMaxRetries(2))
                        .get()
                        .onUrl(NON_EXISTING_PATH)
                        .andExecuteFor()
                        .aResponseEntity()
                        .ofTypeAsync(String)
                        .get()
            } catch (Exception e) {
                return "exception"
            }
        }
    }

}
