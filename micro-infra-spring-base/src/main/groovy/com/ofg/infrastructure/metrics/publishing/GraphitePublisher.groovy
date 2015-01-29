package com.ofg.infrastructure.metrics.publishing

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.GraphiteReporter
import com.codahale.metrics.graphite.GraphiteSender
import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * A publisher to <a href="http://graphite.wikidot.com/">Graphite</a>. Creates a {@link GraphiteReporter} instance
 * that in a given {@link com.ofg.infrastructure.metrics.publishing.PublishingInterval} publishes
 * data to Graphite. 
 * 
 * @see GraphiteReporter
 * @see com.ofg.infrastructure.metrics.publishing.PublishingInterval
 */
@CompileStatic
class GraphitePublisher implements MetricsPublishing {
    private final GraphiteReporter graphiteReporter
    private final PublishingInterval publishingInterval

    GraphitePublisher(GraphiteSender graphite, PublishingInterval publishingInterval, MetricRegistry metricRegistry,
                      TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit) {
        this(graphite, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, MetricFilter.ALL)
    }

    GraphitePublisher(GraphiteSender graphite, PublishingInterval publishingInterval, MetricRegistry metricRegistry,
                      TimeUnit reportedRatesTimeUnit, TimeUnit reportedDurationsTimeUnit, MetricFilter metricFilter) {
        this(graphite, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, metricFilter, null)
    }

    GraphitePublisher(GraphiteSender graphite, PublishingInterval publishingInterval, MetricRegistry metricRegistry,
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

    @Override
    void start() {
        graphiteReporter.start(publishingInterval.interval, publishingInterval.timeUnit)
    }

    @Override
    void stop() {
        graphiteReporter?.stop()
    }
}
