package com.ofg.infrastructure.web.view

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import spock.lang.Specification

import static com.ofg.config.BasicProfiles.TEST

@WebAppConfiguration
@ActiveProfiles(TEST)
@DirtiesContext //context has to be refreshed to notice changes in system properties
@ContextConfiguration(classes = [BaseConfiguration, ConfigurationWithoutServiceDiscovery])
@CompileStatic
abstract class JsonJacksonFeaturesSpec extends Specification {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter

    protected MappingJackson2HttpMessageConverter getFirstConfiguredJacksonMessageConverter() {
        def firstJacksonMessageConverter = requestMappingHandlerAdapter.getMessageConverters().find { it ->
            it instanceof MappingJackson2HttpMessageConverter
        }
        if (firstJacksonMessageConverter) {
            (MappingJackson2HttpMessageConverter)firstJacksonMessageConverter
        } else {
            throw new IllegalStateException("Unable to find predefined instance of MappingJackson2HttpMessageConverter class")
        }
    }
}
