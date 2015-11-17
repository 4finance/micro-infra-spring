package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.correlationid.HeadersSettingFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Trace
import org.springframework.cloud.sleuth.TraceContextHolder
import org.springframework.cloud.sleuth.instrument.web.TraceFilter
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

class MvcCorrelationIdSettingIntegrationSpec extends MvcIntegrationSpec {

    @Autowired protected Trace trace

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilters(new HeadersSettingFilter(), new TraceFilter(trace))
    }

    def setup() {
        TraceContextHolder.removeCurrentSpan()
    }

    def cleanup() {
        TraceContextHolder.removeCurrentSpan()
    }
}
