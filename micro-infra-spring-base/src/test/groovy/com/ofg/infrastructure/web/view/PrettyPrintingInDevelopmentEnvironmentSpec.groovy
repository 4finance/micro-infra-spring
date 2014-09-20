package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import static com.ofg.config.BasicProfiles.DEVELOPMENT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery],
                      loader = SpringApplicationContextLoader)
@ActiveProfiles(DEVELOPMENT)
class PrettyPrintingInDevelopmentEnvironmentSpec extends MvcIntegrationSpec {

    private String PRETTY_PRINTED_RESULT = new ClassPathResource("prettyPrinted.json").inputStream.text.trim()

    def "In development environment pretty printed JSON should be returned"() {
        expect:
            mockMvc.perform(get("/test"))
                .andExpect(content().string(PRETTY_PRINTED_RESULT))
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
    }
}
