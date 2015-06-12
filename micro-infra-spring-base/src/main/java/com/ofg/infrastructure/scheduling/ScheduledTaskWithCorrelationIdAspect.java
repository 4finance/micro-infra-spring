package com.ofg.infrastructure.scheduling;

import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import com.ofg.infrastructure.correlationid.UuidGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.Callable;

/**
 * Aspect that sets correlationId for executions of methods annotated with {@link Scheduled} annotation.
 */
@Aspect
public class ScheduledTaskWithCorrelationIdAspect {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskWithCorrelationIdAspect.class);

    private final UuidGenerator uuidGenerator;

    public ScheduledTaskWithCorrelationIdAspect(UuidGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    @Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
    public Object setNewCorrelationIdOnThread(final ProceedingJoinPoint pjp) throws Throwable {
        String correlationId = uuidGenerator.create();
        return CorrelationIdUpdater.withId(correlationId, new Callable() {
            @Override
            public Object call() throws Exception {
                try {
                    return pjp.proceed();
                } catch (Throwable throwable) {
                    log.error("Failed to proceed with the next advice or target method invocation", throwable);
                    throw new RuntimeException(throwable);
                }
            }
        });
    }
}
