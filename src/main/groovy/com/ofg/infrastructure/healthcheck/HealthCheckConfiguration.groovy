package com.ofg.infrastructure.healthcheck

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@TypeChecked
@Configuration
class HealthCheckConfiguration {
    
    @Bean
    PingController pingController() {
        return new PingController()
    }
    
}
