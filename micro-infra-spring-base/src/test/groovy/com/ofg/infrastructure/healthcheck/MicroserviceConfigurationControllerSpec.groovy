package com.ofg.infrastructure.healthcheck

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [BaseConfiguration, HealthCheckConfiguration, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class MicroserviceConfigurationControllerSpec extends MvcCorrelationIdSettingIntegrationSpec {

    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2185');

    def 'should provide content of configuration file'() {
        expect:
            mockMvc.perform(get('/config'))
                    .andExpect(status().isOk())
                    .andExpect(content().string(equalToIgnoringWhiteSpace(
                        """{
                          "pl" : {
                            "dependencies" : {
                              "confirmation" : {
                                "path" : "foo/bar/security/confirmation"
                              },
                              "foo-bar" : {
                                "path" : "com/ofg/foo/bar"
                              },
                              "newsletter" : {
                                "path" : "foo/bar/comms/newsletter"
                              },
                              "users" : {
                                "path" : "foo/bar/users"
                              }
                            },
                            "this" : "foo/bar/registration"
                          }
                        }"""
            )))
    }

}
