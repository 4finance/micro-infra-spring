package com.ofg.infrastructure.camel;

import com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect;
import org.springframework.cloud.sleuth.IdGenerator;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Configuration that provides {@link CorrelationIdOnCamelRouteAspect}.
 */
@Configuration
@Import(TraceAutoConfiguration.class)
@EnableAspectJAutoProxy
public class CorrelationIdOnCamelRouteConfiguration {

    @Bean
    public CorrelationIdOnCamelRouteAspect correlationIdOnCamelRouteAspect(IdGenerator idGenerator) {
        return new CorrelationIdOnCamelRouteAspect(idGenerator);
    }
}
