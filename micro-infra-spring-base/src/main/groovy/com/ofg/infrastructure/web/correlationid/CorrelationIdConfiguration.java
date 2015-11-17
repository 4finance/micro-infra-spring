package com.ofg.infrastructure.web.correlationid;

import com.ofg.infrastructure.correlationid.UuidGenerator;
import com.ofg.infrastructure.scheduling.TaskSchedulingConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cloud.sleuth.IdGenerator;
import org.springframework.cloud.sleuth.instrument.web.TraceWebAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Registers beans that add correlation id to requests
 *
 * @see CorrelationIdAspect
 */
@Configuration
@Import(TaskSchedulingConfiguration.class)
@AutoConfigureBefore(TraceWebAutoConfiguration.class)
public class CorrelationIdConfiguration {

    @Value("${rest.correlationId.skipPattern:}")
    private String skipPattern;

    @Bean
    public CorrelationIdAspect correlationIdAspect() {
        return new CorrelationIdAspect();
    }

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGenerator();
    }

    @Bean
    public FilterRegistrationBean correlationIdFilter(IdGenerator idGenerator) {
        return new FilterRegistrationBean(new HeadersSettingFilter(idGenerator));
    }
}
