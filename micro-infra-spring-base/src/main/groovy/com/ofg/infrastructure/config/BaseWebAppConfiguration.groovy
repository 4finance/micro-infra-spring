package com.ofg.infrastructure.config

import com.ofg.infrastructure.discovery.ServiceDiscoveryConfiguration
import com.ofg.infrastructure.healthcheck.HealthCheckConfiguration
import com.ofg.infrastructure.metrics.config.MetricsConfiguration
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
 *  <li>{@link ServiceDiscoveryConfiguration} - contains configurations related to service discovery</li>
 *  <li>{@link MetricsConfiguration} - contains configurations with registry of metrics instances
 *  <li>{@link HealthCheckConfiguration} - contains configurations related to Health check verification
 * </ul>
 *
 * @see WebInfrastructureConfiguration
 * @see ServiceDiscoveryConfiguration
 * @see MetricsConfiguration
 * @see HealthCheckConfiguration
 */
@CompileStatic
@Configuration
@Import([WebInfrastructureConfiguration, ServiceDiscoveryConfiguration, MetricsConfiguration, HealthCheckConfiguration])
class BaseWebAppConfiguration {
}
