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
 *  <li>WebInfrastructureConfiguration {@see WebInfrastructureConfiguration} - contains beans related to filter, service communication and web application setup</li>
 *  <li> ServiceDiscoveryConfiguration {@see ServiceDiscoveryConfiguration} - contains all beans related to service discovery</li>
 *  <li> MetricsRegistryConfiguration {@see MetricsRegistryConfiguration} - contains bean with registry of metrics instances
 *  <li> HealthCheckConfiguration {@see HealthCheckConfiguration} - contains beans related to Health check verification
 *  <li> SwaggerConfiguration {@see SwaggerConfiguration} - contains beans related to Swagger API documentation
 * </ul>
 */
@Configuration
@TypeChecked
@Import([WebInfrastructureConfiguration, ServiceDiscoveryConfiguration, MetricsRegistryConfiguration, HealthCheckConfiguration, SwaggerConfiguration])
class WebAppConfiguration {
}
