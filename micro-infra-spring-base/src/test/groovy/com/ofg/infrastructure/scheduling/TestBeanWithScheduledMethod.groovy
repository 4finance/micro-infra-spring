package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import org.springframework.scheduling.annotation.Scheduled

class TestBeanWithScheduledMethod {

    String correlationId

    @Scheduled(fixedDelay=50L)
    void scheduledMethod() {
        correlationId = CorrelationIdHolder.get()
    }

}
