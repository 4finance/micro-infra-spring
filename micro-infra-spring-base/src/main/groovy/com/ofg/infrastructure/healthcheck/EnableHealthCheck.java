package com.ofg.infrastructure.healthcheck;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for health checking.
 *
 * Registers {@link PingController} (the microservice health check controller) and
 * {@link CollaboratorsConnectivityController} (provider of a state of microservice connection with dependent services).
 *
 * @see PingController
 * @see CollaboratorsConnectivityController
 * @see HealthCheckConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HealthCheckConfiguration.class)
public @interface EnableHealthCheck {

}
