package com.ofg.infrastructure.base
import com.ofg.infrastructure.web.correlationid.CorrelationIdFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

class MvcCorrelationIdSettingIntegrationSpec extends MvcIntegrationSpec {

    void setup() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext).
                addFilter(new CorrelationIdFilter()).
                build()
    }
    
}
