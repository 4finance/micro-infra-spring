package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles

import static com.ofg.config.BasicProfiles.PRODUCTION
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@SpringBootTest(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery])
@ActiveProfiles(value = PRODUCTION, inheritProfiles = false)
class NoPrettyPrintingInProductionEnvironmentSpec extends MvcIntegrationSpec {

    String NOT_PRETTY_PRINTED_RESULT = new ClassPathResource("notPrettyPrinted.json").inputStream.text.trim()

    def "should return non-pretty JSON when production profile is active"() {
        expect:
            mockMvc.perform(get("/test"))
                .andExpect(content().string(NOT_PRETTY_PRINTED_RESULT))
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
    }
}
