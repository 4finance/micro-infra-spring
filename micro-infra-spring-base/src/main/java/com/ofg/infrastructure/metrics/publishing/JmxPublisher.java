package com.ofg.infrastructure.metrics.publishing;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * Makes it possible to view metrics via JMX.
 */
public class JmxPublisher implements MetricsPublishing {

    private final JmxReporter jmxReporter;

    public JmxPublisher(MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit) {
        this(metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, MetricFilter.ALL);
    }

    public JmxPublisher(MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit, MetricFilter metricFilter) {
        jmxReporter = JmxReporter
                .forRegistry(metricRegistry)
                .convertRatesTo(reportedRatesTimeUnit)
                .convertDurationsTo(reportedDurationsTimeUnit)
                .filter(metricFilter)
                .build();
    }

    @Override
    public void start() {
        jmxReporter.start();
    }

    @Override
    public void stop() {
        if (jmxReporter != null) {
            jmxReporter.stop();
        }
    }
}
