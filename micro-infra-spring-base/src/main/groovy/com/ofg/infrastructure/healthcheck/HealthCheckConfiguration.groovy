package com.ofg.infrastructure.healthcheck

import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Registers {@link PingController} (the microservice health check controller) and {@link CollaboratorsConnectivityController} (provider of a state of microservice connection with dependent services).
 *
 * @see PingController
 * @see CollaboratorsConnectivityController
 */
@CompileStatic
@Configuration
class HealthCheckConfiguration {

    @Bean
    PingController pingController() {
        return new PingController()
    }

    @Bean
    CollaboratorsConnectivityController collaboratorsConnectivityController(ServiceResolver serviceResolver, PingClient pingClient) {
        return new CollaboratorsConnectivityController(serviceResolver, pingClient)
    }

    @Bean
    PingClient pingClient(ServiceRestClient serviceRestClient) {
        return new PingClient(serviceRestClient)
    }

}
