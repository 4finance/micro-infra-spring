package com.ofg.infrastructure.metrics.publishing
import com.codahale.metrics.JmxReporter
import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * Makes it possible to view metrics via JMS.
 */
@CompileStatic
class JmxPublisher implements MetricsPublishing {
    private final JmxReporter jmxReporter

    JmxPublisher(MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit) {
        this(metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, MetricFilter.ALL)
    }

    JmxPublisher(MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit, MetricFilter metricFilter) {
        jmxReporter = JmxReporter
                .forRegistry(metricRegistry)
                .convertRatesTo(reportedRatesTimeUnit)
                .convertDurationsTo(reportedDurationsTimeUnit)
                .filter(metricFilter)
                .build()
    }

    @Override
    void start() {
        jmxReporter.start()
    }

    @Override
    void stop() {
        jmxReporter?.stop()
    }
}
