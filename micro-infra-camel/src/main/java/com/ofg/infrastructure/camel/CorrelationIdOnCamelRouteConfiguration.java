package com.ofg.infrastructure.camel;

import java.util.Random;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect;
import com.ofg.infrastructure.web.correlationid.EnableCorrelationId;

/**
 * Configuration that provides {@link CorrelationIdOnCamelRouteAspect}.
 */
@Configuration
@EnableCorrelationId
@EnableAspectJAutoProxy
public class CorrelationIdOnCamelRouteConfiguration {

    @Bean
    public CorrelationIdOnCamelRouteAspect correlationIdOnCamelRouteAspect(Random idGenerator, Tracer trace) {
        return new CorrelationIdOnCamelRouteAspect(idGenerator, trace);
    }
}
