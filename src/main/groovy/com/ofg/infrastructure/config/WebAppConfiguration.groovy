package com.ofg.infrastructure.config

import com.ofg.infrastructure.discovery.ServiceDiscoveryConfiguration
import com.ofg.infrastructure.healthcheck.HealthCheckConfiguration
import com.ofg.infrastructure.metrics.registry.MetricsRegistryConfiguration
import com.ofg.infrastructure.web.config.SwaggerConfiguration
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@TypeChecked
@Import([WebInfrastructureConfiguration, ServiceDiscoveryConfiguration, MetricsRegistryConfiguration, HealthCheckConfiguration, SwaggerConfiguration])
class WebAppConfiguration {
}
