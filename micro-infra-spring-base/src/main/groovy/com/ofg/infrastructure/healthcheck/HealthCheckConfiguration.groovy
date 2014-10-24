package com.ofg.infrastructure.healthcheck

import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
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

    @Bean
    CollaboratorsConnectivityController collaboratorsConnectivityController(ServiceRestClient serviceRestClient, ServiceResolver serviceResolver) {
        return new CollaboratorsConnectivityController(serviceRestClient, serviceResolver)
    }

}
