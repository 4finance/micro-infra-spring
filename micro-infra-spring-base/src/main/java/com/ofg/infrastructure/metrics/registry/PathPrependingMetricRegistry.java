package com.ofg.infrastructure.metrics.registry;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Custom implementation of {@link MetricRegistry} that prepends if necessary
 * 4finance path prefix to a given metric name.
 *
 * @see com.ofg.infrastructure.metrics.registry.MetricPathProvider
 */
public class PathPrependingMetricRegistry extends MetricRegistry {

    private final MetricPathProvider metricPathProvider;

    public PathPrependingMetricRegistry(MetricPathProvider metricPathProvider) {
        this.metricPathProvider = metricPathProvider;
    }

    @Override
    public <T extends Metric> T register(String metricName, T metric) throws IllegalArgumentException {
        if (metricPathProvider.isPathPrepended(metricName)) {
            return super.register(metricName, metric);
        }
        return super.register(metricPathProvider.getMetricPath(metricName), metric);
    }

    @Override
    public Counter counter(String metricName) {
        return super.counter(metricPathProvider.getMetricPath(metricName));
    }

    @Override
    public Histogram histogram(String metricName) {
        return super.histogram(metricPathProvider.getMetricPath(metricName));
    }

    @Override
    public Timer timer(String metricName) {
        return super.timer(metricPathProvider.getMetricPath(metricName));
    }

    @Override
    public boolean remove(String metricName) {
        return super.remove(metricPathProvider.getMetricPath(metricName));
    }

    @Override
    public Meter meter(String metricName) {
        return super.meter(metricPathProvider.getMetricPath(metricName));
    }

}