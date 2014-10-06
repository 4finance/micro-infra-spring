package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import org.apache.camel.CamelContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@CompileStatic
class CamelRouteConfiguration {

    @Bean
    CamelRouteModifier camelRouteModifier(CamelContext camelContext) {
        return new CamelRouteModifier(camelContext)
    }

}
