package com.ofg.infrastructure.camel;

import java.util.Random;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect;

/**
 * Configuration that provides {@link CorrelationIdOnCamelRouteAspect}.
 */
@Configuration
@Import(TraceAutoConfiguration.class)
@EnableAspectJAutoProxy
public class CorrelationIdOnCamelRouteConfiguration {

    @Bean
    public CorrelationIdOnCamelRouteAspect correlationIdOnCamelRouteAspect(Random idGenerator, Tracer trace) {
        return new CorrelationIdOnCamelRouteAspect(idGenerator, trace);
    }
}
