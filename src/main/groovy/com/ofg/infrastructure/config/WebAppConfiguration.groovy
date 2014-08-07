package com.ofg.infrastructure.config

import com.ofg.infrastructure.healthcheck.HealthCheckConfiguration
import com.ofg.infrastructure.metrics.registry.MetricsRegistryConfiguration
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@TypeChecked
@Import([WebInfrastructureConfiguration, MetricsRegistryConfiguration, HealthCheckConfiguration])
class WebAppConfiguration {
}
