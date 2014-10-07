package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import org.apache.camel.CamelContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@CompileStatic
class CamelRouteConfiguration {

    @Bean(initMethod = 'addCorrelationIdInterception')
    CorrelationIdOnCamelRouteSetter camelRouteModifier(CamelContext camelContext) {
        return new CorrelationIdOnCamelRouteSetter(camelContext)
    }

}
