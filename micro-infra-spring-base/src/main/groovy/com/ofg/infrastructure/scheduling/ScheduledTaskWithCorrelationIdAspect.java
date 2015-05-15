package com.ofg.infrastructure.scheduling;

import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import com.ofg.infrastructure.correlationid.UuidGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

/**
 * Aspect that sets correlationId for running threads executing methods annotated with {@link Scheduled} annotation.
 * For every execution of scheduled method a new, i.e. unique one, value of correlationId will be set.
 */
@Aspect
public class ScheduledTaskWithCorrelationIdAspect {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
                    log.error("Didn't manage to proceed with the pointcut", throwable);
                    throw new RuntimeException(throwable);
                }
            }
        });
    }
}
