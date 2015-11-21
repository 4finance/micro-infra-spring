package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import org.springframework.cloud.sleuth.Span
import org.springframework.scheduling.annotation.Scheduled

class TestBeanWithScheduledMethod {

    Span span

    @Scheduled(fixedDelay=50L)
    void scheduledMethod() {
        span = CorrelationIdHolder.get()
    }

}
