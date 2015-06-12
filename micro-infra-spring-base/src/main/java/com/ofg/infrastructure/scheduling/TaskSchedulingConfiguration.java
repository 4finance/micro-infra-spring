package com.ofg.infrastructure.scheduling;

import com.ofg.infrastructure.correlationid.UuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Registers {@link ScheduledTaskWithCorrelationIdAspect}
 */
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
public class TaskSchedulingConfiguration {

    @Bean
    public ScheduledTaskWithCorrelationIdAspect scheduledTaskPointcut(UuidGenerator uuidGenerator) {
        return new ScheduledTaskWithCorrelationIdAspect(uuidGenerator);
    }
}
