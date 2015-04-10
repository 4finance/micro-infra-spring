package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.correlationid.CorrelationIdUpdaterUtil
import com.ofg.infrastructure.correlationid.UuidGenerator
import groovy.transform.CompileStatic
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

/**
 * Aspect that sets correlationId for running threads executing methods annotated with {@link org.springframework.scheduling.annotation.Scheduled} annotation.
 * For every execution of scheduled method a new, i.e. unique one, value of correlationId will be set.
 */
@Aspect
@CompileStatic
class ScheduledTaskWithCorrelationIdAspect {

    private final UuidGenerator uuidGenerator

    ScheduledTaskWithCorrelationIdAspect(UuidGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator
    }

    @Around('execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))')
    Object setNewCorrelationIdOnThread(ProceedingJoinPoint pjp) throws Throwable {
        String correlationId = uuidGenerator.create()
        return CorrelationIdUpdaterUtil.withId(correlationId) {
            return pjp.proceed()
        }
    }

}