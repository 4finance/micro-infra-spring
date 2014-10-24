package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery],
        loader = SpringApplicationContextLoader)
class SentJSONObjectsShouldBeCorrectlyDeserializedSpec extends MvcCorrelationIdSettingIntegrationSpec {

    def "should correctly deserialize request to SampleBean instance when valid JSON document is sent to controller"() {
        expect:
            mockMvc.perform(post("/test")
                .contentType(APPLICATION_JSON)
                .content('{"sampleField":"sampleValue"}'))
                .andExpect(status().is(HttpStatus.CREATED.value()))
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
    }
}
