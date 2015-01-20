package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.correlationid.CorrelationIdFilter
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

@ContextConfiguration(classes = [ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
abstract class MicroserviceMvcWiremockSpec extends MvcWiremockIntegrationSpec {

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilter(new CorrelationIdFilter())
    }
}
