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
                    .andExpect(content().string(equalToIgnoringWhiteSpace('{\n' +
                                                                            '  "pl" : {\n' +
                                                                            '    "dependencies" : {\n' +
                                                                            '      "confirmation" : {\n' +
                                                                            '        "path" : "foo/bar/security/confirmation"\n' +
                                                                            '      },\n' +
                                                                            '      "foo-bar" : {\n' +
                                                                            '        "path" : "com/ofg/foo/bar"\n' +
                                                                            '      },\n' +
                                                                            '      "newsletter" : {\n' +
                                                                            '        "path" : "foo/bar/comms/newsletter"\n' +
                                                                            '      },\n' +
                                                                            '      "users" : {\n' +
                                                                            '        "path" : "foo/bar/users"\n' +
                                                                            '      }\n' +
                                                                            '    },\n' +
                                                                            '    "this" : "foo/bar/registration"\n' +
                                                                            '  }\n' +
                                                                            '}')))
    }

}
