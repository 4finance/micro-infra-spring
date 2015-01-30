package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.correlationid.UuidGenerator
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Registers beans related to task scheduling.
 *
 * @see ScheduledTaskWithCorrelationIdAspect
 */
@CompileStatic
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
class TaskSchedulingConfiguration {

    @Bean ScheduledTaskWithCorrelationIdAspect scheduledTaskPointcut(UuidGenerator uuidGenerator) {
        return new ScheduledTaskWithCorrelationIdAspect(uuidGenerator)
    }
}
