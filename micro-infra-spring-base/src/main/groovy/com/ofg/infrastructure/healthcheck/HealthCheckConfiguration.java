package com.ofg.infrastructure.healthcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

/**
 * Registers {@link PingController} (the microservice health check controller) and {@link CollaboratorsConnectivityController} (provider of a state of microservice connection with dependent services).
 *
 * @see PingController
 * @see CollaboratorsConnectivityController
 */
@Configuration
@Import(CollaboratorsConfiguration.class)
public class HealthCheckConfiguration {
    @Bean
    public PingController pingController() {
        return new PingController();
    }

    @Bean
    public CollaboratorsConnectivityController collaboratorsConnectivityController(CollaboratorsStatusResolver collaboratorsStatusResolver) {
        return new CollaboratorsConnectivityController(collaboratorsStatusResolver);
    }

    @Bean
    public MicroserviceConfigurationController microserviceConfigurationController(@Value("${microservice.config.file:classpath:microservice.json}") Resource microserviceConfig) {
        return new MicroserviceConfigurationController(microserviceConfig);
    }

}
