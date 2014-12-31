package com.ofg.infrastructure.healthcheck

import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [BaseConfiguration, HealthCheckConfiguration, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class MicroserviceConfigurationControllerSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2185');

    def 'should provide content of configuration file'() {
        given:
            URL resource = Resources.getResource('microservice.json')
            String expectedContent = Resources.toString(resource, Charsets.UTF_8)
        expect:
            mockMvc.perform(get('/microservice.json'))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath('$.configuration', is(equalToIgnoringWhiteSpace(expectedContent))))
    }

}
