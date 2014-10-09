package com.ofg.infrastructure.camel.config

import com.ofg.infrastructure.camel.CorrelationIdOnCamelRouteConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportResource

@Configuration
@ImportResource("classpath:spring/camel-context.xml")
@Import(CorrelationIdOnCamelRouteConfiguration.class)
class CamelRouteAsBeanConfiguration {

    @Bean
    TestRouteBuilder testRouteBuilder() {
        return new TestRouteBuilder()
    }

}
