package com.ofg.infrastructure.metrics.publishing

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * Defines publishing interval for metrics publisher
  *
 * @param interval - amount of time between publications
 * @param timeUnit - time unit for {@code interval}
 */
@CompileStatic
class PublishingInterval {
    final long interval
    final TimeUnit timeUnit

    PublishingInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval
        this.timeUnit = timeUnit
    }
}
