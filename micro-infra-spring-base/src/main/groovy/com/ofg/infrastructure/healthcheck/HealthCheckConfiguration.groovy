package com.ofg.infrastructure.healthcheck

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Registers {@link PingController} - a health check controller 
 * 
 * @see PingController
 */
@CompileStatic
@Configuration
class HealthCheckConfiguration {
    
    @Bean
    PingController pingController() {
        return new PingController()
    }
    
}
