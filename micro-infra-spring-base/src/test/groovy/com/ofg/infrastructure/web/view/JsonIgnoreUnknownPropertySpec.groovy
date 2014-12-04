package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [JsonIgnoreUnknownPropertySpec.Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery],
        loader = SpringApplicationContextLoader)
class JsonIgnoreUnknownPropertySpec extends MvcCorrelationIdSettingIntegrationSpec {

    private static final String JSON_WITH_ADDITIONAL_FIELDS = '{"sampleField":"sampleValue", "unknownField":"unknownValue"}'

    def "should ignore unknown field"() {
        expect:
            mockMvc.perform(post("/test")
                    .contentType(APPLICATION_JSON)
                    .content(JSON_WITH_ADDITIONAL_FIELDS))
                    .andExpect(status().isCreated())
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
    }
}
