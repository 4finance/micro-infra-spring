package com.ofg.infrastructure.metrics.registry

import com.codahale.metrics.*
import groovy.transform.CompileStatic

/**
 * Custom implementation of {@link MetricRegistry} that prepends if necessary
 * 4finance path prefix to a given metric name.
 * 
 * @see MetricPathProvider
 */
@CompileStatic
class PathPrependingMetricRegistry extends MetricRegistry {

    private final MetricPathProvider metricPathProvider

    PathPrependingMetricRegistry(MetricPathProvider metricPathProvider) {
        this.metricPathProvider = metricPathProvider
    }
    
    @Override
    public <T extends Metric> T register(String metricName, T metric) throws IllegalArgumentException {
        if (metricPathProvider.isPathPrepended(metricName)) {
            return super.register(metricName, metric)
        }
        return super.register(metricPathProvider.getMetricPath(metricName), metric)
    }

    @Override
    Counter counter(String metricName) {
        return super.counter(metricPathProvider.getMetricPath(metricName))
    }

    @Override
    Histogram histogram(String metricName) {
        return super.histogram(metricPathProvider.getMetricPath(metricName))
    }

    @Override
    Timer timer(String metricName) {
        return super.timer(metricPathProvider.getMetricPath(metricName))
    }

    @Override
    boolean remove(String metricName) {
        return super.remove(metricPathProvider.getMetricPath(metricName))
    }

    @Override
    Meter meter(String metricName) {
        return super.meter(metricPathProvider.getMetricPath(metricName))
    }
}
