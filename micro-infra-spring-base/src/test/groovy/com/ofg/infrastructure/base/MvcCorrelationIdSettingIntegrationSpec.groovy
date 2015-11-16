package com.ofg.infrastructure.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.instrument.web.TraceFilter
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

class MvcCorrelationIdSettingIntegrationSpec extends MvcIntegrationSpec {

    @Autowired protected TraceFilter traceFilter

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilter(traceFilter)
    }
}
