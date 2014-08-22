package com.ofg.infrastructure.web.exception

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

import static org.hamcrest.Matchers.equalTo
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [Config, BaseConfiguration, ConfigurationWithoutServiceDiscovery], loader = SpringApplicationContextLoader)
class ExceptionHandlingMvcSpec extends MvcIntegrationSpec {
    
    def "should return bad request error for missing last name"() {
        expect:
            mockMvc.perform(post("/test").contentType(APPLICATION_JSON)
                .content('{}'))
                .andExpect(status().is(BAD_REQUEST.value()))
                .andExpect(jsonPath('$[0].field', equalTo("shouldBeTrue")))
                .andExpect(jsonPath('$[0].message', equalTo("must be true")))
    }
    
    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
        
    }
}
