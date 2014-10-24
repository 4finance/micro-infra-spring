package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.correlationid.CorrelationIdFilter
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ContextConfiguration(classes = [ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class MicroserviceMvcWiremockSpec extends MvcWiremockIntegrationSpec {

    void setup() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext).
                addFilter(new CorrelationIdFilter()).
                build()
    }

}
