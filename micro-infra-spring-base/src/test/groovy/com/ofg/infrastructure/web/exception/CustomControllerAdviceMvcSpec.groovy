package com.ofg.infrastructure.web.exception

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcCorrelationIdSettingIntegrationSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery])
class CustomControllerAdviceMvcSpec extends MvcCorrelationIdSettingIntegrationSpec {

    def "should apply custom logic when application uses its own @ControllerAdvice"() {
        expect:
            mockMvc.perform(get("/testLowestPrecedence"))
                .andExpect(status().is(SERVICE_UNAVAILABLE.value()))
                .andExpect(header().string("SAMPLE_HEADER", "SAMPLE_HEADER_VALUE"))
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }

        @Bean
        CustomControllerAdvice customControllerAdvice() {
            return new CustomControllerAdvice()
        }
    }

}
