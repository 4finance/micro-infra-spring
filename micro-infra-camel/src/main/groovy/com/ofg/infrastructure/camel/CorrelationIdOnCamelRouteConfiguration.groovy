package com.ofg.infrastructure.camel

import com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect
import com.ofg.infrastructure.correlationid.UuidGenerator
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * Configuration that provides {@link CorrelationIdOnCamelRouteAspect}.
 */
@Configuration
@CompileStatic
@EnableAspectJAutoProxy
class CorrelationIdOnCamelRouteConfiguration {

    @Bean
    CorrelationIdOnCamelRouteAspect correlationIdOnCamelRouteAspect(UuidGenerator uuidGenerator) {
        return new CorrelationIdOnCamelRouteAspect(uuidGenerator)
    }
}
