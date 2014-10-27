package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import groovy.transform.CompileStatic
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before

/**
 * Aspect that sets correlationId for running threads executing methods annotated with {@link org.springframework.scheduling.annotation.Scheduled} annotation.
 * For every execution of scheduled method a new, i.e. unique one, value of correlationId will be set.
 */
@Aspect
@CompileStatic
class ScheduledTaskWithCorrelationIdAspect {

    @Before('execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))')
    void setNewCorrelationIdOnThread(JoinPoint joinPoint) throws Throwable {
        String correlationId = UUID.randomUUID().toString()
        CorrelationIdUpdater.updateCorrelationId(correlationId)
    }

}