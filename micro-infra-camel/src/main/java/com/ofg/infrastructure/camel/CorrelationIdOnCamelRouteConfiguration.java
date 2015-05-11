package com.ofg.infrastructure.camel;

import com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect;
import com.ofg.infrastructure.correlationid.UuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration that provides {@link CorrelationIdOnCamelRouteAspect}.
 */
@Configuration
@EnableAspectJAutoProxy
public class CorrelationIdOnCamelRouteConfiguration {

    @Bean
    public CorrelationIdOnCamelRouteAspect correlationIdOnCamelRouteAspect(UuidGenerator uuidGenerator) {
        return new CorrelationIdOnCamelRouteAspect(uuidGenerator);
    }
}
