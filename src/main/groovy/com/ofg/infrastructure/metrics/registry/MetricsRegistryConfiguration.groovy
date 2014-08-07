package com.ofg.infrastructure.metrics.registry

import com.codahale.metrics.MetricRegistry
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@TypeChecked
class MetricsRegistryConfiguration {

    @Bean
    MetricRegistry metricRegistry() {
        return new MetricRegistry()
    }
}
