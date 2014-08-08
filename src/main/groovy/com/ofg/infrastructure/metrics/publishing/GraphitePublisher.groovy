package com.ofg.infrastructure.metrics.publishing

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteReporter
import groovy.transform.TypeChecked

import java.util.concurrent.TimeUnit

@TypeChecked
class GraphitePublisher {
    private final GraphiteReporter graphiteReporter
    private final PublishingInterval publishingInterval

    GraphitePublisher(Graphite graphite, PublishingInterval publishingInterval, MetricRegistry metricRegistry,
                      TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit) {
        this(graphite, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, MetricFilter.ALL)
    }

    GraphitePublisher(Graphite graphite, PublishingInterval publishingInterval, MetricRegistry metricRegistry,
                      TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit, MetricFilter metricFilter) {
        this(graphite, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, metricFilter, null)
    }

    GraphitePublisher(Graphite graphite, PublishingInterval publishingInterval, MetricRegistry metricRegistry,
                      TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit, MetricFilter metricFilter, String metricsPrefix) {
        graphiteReporter = GraphiteReporter
                .forRegistry(metricRegistry)
                .convertRatesTo(reportedRatesTimeUnit)
                .convertDurationsTo(reportedDurationsTimeUnit)
                .filter(metricFilter)
                .prefixedWith(metricsPrefix)
                .build(graphite)
        this.publishingInterval = publishingInterval
    }

    void start() {
        graphiteReporter.start(publishingInterval.interval, publishingInterval.timeUnit)
    }

    void stop() {
        graphiteReporter?.stop()
    }

    static class PublishingInterval {
        final long interval
        final TimeUnit timeUnit

        PublishingInterval(long interval, TimeUnit timeUnit) {
            this.interval = interval
            this.timeUnit = timeUnit
        }
    }
}
