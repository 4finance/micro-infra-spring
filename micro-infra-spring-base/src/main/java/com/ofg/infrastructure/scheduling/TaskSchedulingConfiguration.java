package com.ofg.infrastructure.scheduling;

import org.springframework.cloud.sleuth.IdGenerator;
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
    public ScheduledTaskWithCorrelationIdAspect scheduledTaskPointcut(IdGenerator uuidGenerator) {
        return new ScheduledTaskWithCorrelationIdAspect(uuidGenerator);
    }
}
