package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import org.apache.camel.builder.RouteBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration that registers {@link CorrelationIdOnCamelRouteSetter} as a Spring bean.
 * The bean updates all route definitions provided in {@link RouteBuilder builders} with interceptor that sets
 * correlationId in all received and sent messages.
 */
@Configuration
@CompileStatic
class CorrelationIdOnCamelRouteConfiguration {

    /**
     * Provides a bean responsible for setting correlationId handling in Camel's messages.
     * CorrelationId handling is set inside init method of the bean so usage of the mechanism
     * is transparent to the end user.
     *
     * @param builders all defined route builders
     * @return setter bean of correlationId interceptor on Camel's routes
     */
    @Bean(initMethod = 'addCorrelationIdInterception')
    CorrelationIdOnCamelRouteSetter camelRouteModifier(List<RouteBuilder> builders) {
        return new CorrelationIdOnCamelRouteSetter(builders)
    }

}
