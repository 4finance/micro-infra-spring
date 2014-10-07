package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import org.apache.camel.CamelContext
import org.apache.camel.model.ModelCamelContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration that registers {@link CorrelationIdOnCamelRouteSetter} as a Spring bean.
 * The bean updates all route definitions in provided {@link ModelCamelContext} with interceptor that sets
 * correlationId in all received and sent messages.
 */
@Configuration
@CompileStatic
class CamelRouteConfiguration {

    /**
     * Provides existing {@code camelContext} as {@link ModelCamelContext}.
     *
     * @param camelContext available Camel context
     * @return {@code camelContext} as {@link ModelCamelContext}
     */
    @Bean
    ModelCamelContext modelCamelContext(CamelContext camelContext) {
        return (ModelCamelContext)camelContext
    }

    /**
     * Provides a bean responsible for setting correlationId handling in Camel's messages.
     * CorrelationId handling is set inside init method of the bean so usage of the mechanism
     * is transparent to the end user.
     *
     * @param camelContext provides all defined routes
     * @return setter bean of correlationId interceptor on Camel's routes
     */
    @Bean(initMethod = 'addCorrelationIdInterception')
    CorrelationIdOnCamelRouteSetter camelRouteModifier(ModelCamelContext camelContext) {
        return new CorrelationIdOnCamelRouteSetter(camelContext)
    }

}
