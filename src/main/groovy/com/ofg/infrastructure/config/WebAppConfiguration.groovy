package com.ofg.infrastructure.config

import com.ofg.infrastructure.discovery.ServiceDiscoveryConfiguration
import com.ofg.infrastructure.healthcheck.HealthCheckConfiguration
import com.ofg.infrastructure.metrics.registry.MetricsRegistryConfiguration
import com.ofg.infrastructure.web.config.SwaggerConfiguration
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Imports:
 * <ul>
 *  <li>{@link WebInfrastructureConfiguration} - contains beans related to filter, service communication and web application setup</li>
 *  <li>{@link ServiceDiscoveryConfiguration} - contains all beans related to service discovery</li>
 *  <li>{@link MetricsRegistryConfiguration} - contains bean with registry of metrics instances
 *  <li>{@link HealthCheckConfiguration} - contains beans related to Health check verification
 *  <li>{@link SwaggerConfiguration} - contains beans related to Swagger API documentation
 * </ul>
 *
 * @see WebInfrastructureConfiguration
 * @see ServiceDiscoveryConfiguration
 * @see MetricsRegistryConfiguration
 * @see HealthCheckConfiguration
 * @see SwaggerConfiguration
 */
@Configuration
@TypeChecked
@Import([WebInfrastructureConfiguration, ServiceDiscoveryConfiguration, MetricsRegistryConfiguration, HealthCheckConfiguration, SwaggerConfiguration])
class WebAppConfiguration {
}
