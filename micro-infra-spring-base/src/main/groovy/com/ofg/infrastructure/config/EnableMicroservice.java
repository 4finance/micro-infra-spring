package com.ofg.infrastructure.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for the full stack of microservice infrastructure.
 *
 * Imports:
 * <ul>
 *  <li>{@link com.ofg.infrastructure.web.config.WebInfrastructureConfiguration} - contains configurations related to filter, service communication and web application setup</li>
 * </ul>
 *
 * Enables:
 * <ul>
 *  <lI>{@link com.ofg.infrastructure.discovery.EnableServiceDiscovery}</lI>
 *  <lI>{@link com.ofg.infrastructure.metrics.config.EnableMetrics}</lI>
 *  <lI>{@link com.ofg.infrastructure.healthcheck.EnableHealthCheck}</lI>
 * </ul>
 *
 * @see com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
 * @see com.ofg.infrastructure.discovery.EnableServiceDiscovery
 * @see com.ofg.infrastructure.metrics.config.EnableMetrics
 * @see com.ofg.infrastructure.healthcheck.EnableHealthCheck
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(BaseWebAppConfiguration.class)
public @interface EnableMicroservice {

}
