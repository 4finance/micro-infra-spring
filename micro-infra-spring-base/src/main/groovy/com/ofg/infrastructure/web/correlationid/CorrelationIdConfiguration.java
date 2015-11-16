package com.ofg.infrastructure.web.correlationid;

import com.ofg.infrastructure.correlationid.UuidGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers beans that add correlation id to requests
 *
 * @see CorrelationIdAspect
 */
@Configuration
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
}
