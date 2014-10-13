package com.ofg.infrastructure.metrics.publishing

import com.codahale.metrics.Clock
import com.codahale.metrics.CsvReporter
import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * Publishes metrics to CSV file
 * 
 * @see CsvReporter
 * @see com.ofg.infrastructure.metrics.publishing.PublishingInterval
 */
@CompileStatic
class CsvPublisher implements MetricsPublishing {
    private final CsvReporter csvReporter
    private final PublishingInterval publishingInterval

    CsvPublisher(File outputDirectory, PublishingInterval publishingInterval, MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit,
                 TimeUnit reportedDurationsTimeUnit) {
        this(outputDirectory, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, Locale.default, Clock.defaultClock(),)
    }

    CsvPublisher(File outputDirectory, PublishingInterval publishingInterval, MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit,
                 TimeUnit reportedDurationsTimeUnit, Locale locale) {
        this(outputDirectory, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, locale, Clock.defaultClock(),)
    }

    CsvPublisher(File outputDirectory, PublishingInterval publishingInterval, MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit,
                 TimeUnit reportedDurationsTimeUnit, Locale locale, Clock clock) {
        this(outputDirectory, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, locale, clock, MetricFilter.ALL)
    }

    CsvPublisher(File outputDirectory, PublishingInterval publishingInterval, MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit,
                 TimeUnit reportedDurationsTimeUnit, Locale locale, MetricFilter metricFilter) {
        this(outputDirectory, publishingInterval, metricRegistry, reportedRatesTimeUnit, reportedDurationsTimeUnit, locale, Clock.defaultClock(), metricFilter)
    }

    CsvPublisher(File outputDirectory, PublishingInterval publishingInterval, MetricRegistry metricRegistry, TimeUnit reportedRatesTimeUnit,
                 TimeUnit reportedDurationsTimeUnit, Locale locale, Clock clock, MetricFilter metricFilter) {
        checkIfOutputDirectoryExists(outputDirectory)
        csvReporter = CsvReporter
                .forRegistry(metricRegistry)
                .convertRatesTo(reportedRatesTimeUnit)
                .convertDurationsTo(reportedDurationsTimeUnit)
                .formatFor(locale)
                .withClock(clock)
                .filter(metricFilter)
                .build(outputDirectory)
        this.publishingInterval = publishingInterval
    }

    @Override
    void start() {
        csvReporter.start(publishingInterval.interval, publishingInterval.timeUnit)
    }

    @Override
    void stop() throws IOException {
        csvReporter?.stop()
    }

    private void checkIfOutputDirectoryExists(File outputDirectory) {
        if (!outputDirectory.exists() || !outputDirectory.isDirectory()) {
            throw new OutputDirectoryDoesNotExists(outputDirectory)
        }
    }
}
