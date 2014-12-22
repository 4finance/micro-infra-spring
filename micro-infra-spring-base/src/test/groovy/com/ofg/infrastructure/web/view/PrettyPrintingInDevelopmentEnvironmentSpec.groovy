package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import static com.ofg.config.BasicProfiles.DEVELOPMENT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery],
                      loader = SpringApplicationContextLoader)
@ActiveProfiles(DEVELOPMENT)
class PrettyPrintingInDevelopmentEnvironmentSpec extends MvcCorrelationIdSettingIntegrationSpec {

    private static final String CRLF = "\r\n"
    private static final String LF = "\n"
    String PRETTY_PRINTED_RESULT = new ClassPathResource("prettyPrinted.json").inputStream.text.trim()

    def "should return pretty JSON when development profile is active"() {
        expect:
            mockMvc.perform(get("/test"))
                .andExpect({result ->
                    assert PRETTY_PRINTED_RESULT == result.getResponse().getContentAsString().replace(CRLF, LF), "Response content"
            })
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
    }
}
