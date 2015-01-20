package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.correlationid.CorrelationIdFilter
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

class MvcCorrelationIdSettingIntegrationSpec extends MvcIntegrationSpec {

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilter(new CorrelationIdFilter())
    }
}
