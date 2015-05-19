package com.ofg.infrastructure.healthcheck;

import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers {@link PingController} (the microservice health check controller) and {@link CollaboratorsConnectivityController} (provider of a state of microservice connection with dependent services).
 *
 * @see PingController
 * @see CollaboratorsConnectivityController
 */
@Configuration
public class CollaboratorsConfiguration {
    @Bean
    public CollaboratorsStatusResolver collaboratorsStatusResolver(ServiceResolver serviceResolver, PingClient pingClient) {
        return new CollaboratorsStatusResolver(serviceResolver, pingClient);
    }

    @Bean
    public PingClient pingClient(ServiceRestClient serviceRestClient) {
        return new PingClient(serviceRestClient);
    }

}
