package com.ofg.infrastructure.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.cloud.sleuth.instrument.web.TraceFilter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

@ContextConfiguration(classes = [ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
abstract class MicroserviceMvcWiremockSpec extends MvcWiremockIntegrationSpec {

    @Autowired protected TraceFilter traceFilter

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilter(traceFilter)
    }
}
