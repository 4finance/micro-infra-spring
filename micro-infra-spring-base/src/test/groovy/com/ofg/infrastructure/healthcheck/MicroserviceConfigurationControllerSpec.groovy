package com.ofg.infrastructure.healthcheck

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [BaseConfiguration, HealthCheckConfiguration, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class MicroserviceConfigurationControllerSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2185');

    /**
     * Configuration of test microservice comes from microservice.json file and its content is expected in the body content.
     */
    def 'should provide content of configuration file'() {
        expect:
            mockMvc.perform(get('/microservice.json'))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath('$.pl.this', is(equalTo('foo/bar/registration'))))
                    .andExpect(jsonPath('$.pl.dependencies.confirmation.path', is(equalTo('foo/bar/security/confirmation'))))
                    .andExpect(jsonPath('$.pl.dependencies.foo-bar.path', is(equalTo('com/ofg/foo/bar'))))
                    .andExpect(jsonPath('$.pl.dependencies.newsletter.path', is(equalTo('foo/bar/comms/newsletter'))))
                    .andExpect(jsonPath('$.pl.dependencies.users.path', is(equalTo('foo/bar/users'))))
    }

}
