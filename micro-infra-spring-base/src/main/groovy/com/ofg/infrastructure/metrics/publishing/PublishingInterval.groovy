package com.ofg.infrastructure.metrics.publishing

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
class PublishingInterval {
    final long interval
    final TimeUnit timeUnit

    PublishingInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval
        this.timeUnit = timeUnit
    }
}
