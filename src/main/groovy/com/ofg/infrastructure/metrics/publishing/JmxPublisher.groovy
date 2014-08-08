package com.ofg.infrastructure.metrics.publishing

import com.codahale.metrics.JmxReporter
import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.transform.TypeChecked

import java.util.concurrent.TimeUnit

@TypeChecked
class JmxPublisher {
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

    void start() {
        jmxReporter.start()
    }

    void stop() {
        jmxReporter?.stop()
    }
}
