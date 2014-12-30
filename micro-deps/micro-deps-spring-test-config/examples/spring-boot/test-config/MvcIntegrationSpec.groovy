package com.ofg.base

import com.ofg.microservice.infrastructure.correlationid.CorrelationIdFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static com.ofg.microservice.config.Profiles.TEST

@WebAppConfiguration
@ContextConfiguration(classes = [ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
@ActiveProfiles(TEST)
abstract class MvcIntegrationSpec extends Specification {
    
    @Autowired private WebApplicationContext webApplicationContext
    @Autowired private ApplicationContext applicationContext
    protected MockMvc mockMvc

    void setup() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext).
                addFilter(new CorrelationIdFilter()).
                build()
    }

    def cleanup() {
    }

}