package com.ofg.infrastructure.web.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import spock.lang.Specification

import static com.ofg.config.BasicProfiles.TEST

@WebAppConfiguration
@ActiveProfiles(TEST)
abstract class JsonJacksonFeaturesSpec extends Specification {

    @Autowired WebApplicationContext webApplicationContext

    protected MappingJackson2HttpMessageConverter getPredefinedJacksonMessageConverter() {
        def mappingHandlerAdapter = webApplicationContext.getBean(RequestMappingHandlerAdapter.class)
        def jacksonMessageConverter = mappingHandlerAdapter.getMessageConverters().find { it -> it instanceof MappingJackson2HttpMessageConverter }
        if (jacksonMessageConverter) {
            return jacksonMessageConverter
        } else {
            throw new IllegalStateException("Unable to find predefined instance of MappingJackson2HttpMessageConverter class.")
        }
    }

    @Configuration
    static class Config {
        @Bean
        TestController testController() {
            return new TestController()
        }
    }

}
