package com.ofg.infrastructure.camel

import com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect
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
    CorrelationIdOnCamelRouteAspect correlationIdOnCamelRouteAspect() {
        return new CorrelationIdOnCamelRouteAspect()
    }
}
