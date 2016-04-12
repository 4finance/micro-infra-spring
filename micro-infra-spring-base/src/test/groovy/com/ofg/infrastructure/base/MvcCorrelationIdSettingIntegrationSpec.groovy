package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.correlationid.HeadersSettingFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.instrument.web.TraceFilter
import org.springframework.cloud.sleuth.trace.SpanContextHolder
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

class MvcCorrelationIdSettingIntegrationSpec extends MvcIntegrationSpec {

    @Autowired protected TraceFilter filter

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilters(new HeadersSettingFilter(), filter)
    }

    def setup() {
        SpanContextHolder.removeCurrentSpan()
    }

    def cleanup() {
        SpanContextHolder.removeCurrentSpan()
    }
}
