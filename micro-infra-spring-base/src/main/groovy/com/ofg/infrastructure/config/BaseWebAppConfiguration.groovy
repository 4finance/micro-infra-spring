package com.ofg.infrastructure.config

import com.ofg.infrastructure.discovery.EnableServiceDiscovery
import com.ofg.infrastructure.healthcheck.EnableHealthCheck
import com.ofg.infrastructure.metrics.config.EnableMetrics
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration that gives you the full stack of microservice infrastructure. Below you can find a list of
 * imported configurations and their purpose
 *
 * Imports:
 * <ul>
 *  <li>{@link WebInfrastructureConfiguration} - contains configurations related to filter, service communication and web application setup</li>
 * </ul>
 *
 * Enables:
 * <ul>
 *  <lI>{@link com.ofg.infrastructure.discovery.EnableServiceDiscovery}</lI>
 *  <lI>{@link com.ofg.infrastructure.metrics.config.EnableMetrics}</lI>
 *  <lI>{@link com.ofg.infrastructure.healthcheck.EnableHealthCheck}</lI>
 * </ul>
 *
 * @see WebInfrastructureConfiguration
 * @see EnableServiceDiscovery
 * @see EnableMetrics
 * @see EnableHealthCheck
 */
@CompileStatic
@Configuration
@EnableHealthCheck
@EnableMetrics
@Import([WebInfrastructureConfiguration])
class BaseWebAppConfiguration {
}
