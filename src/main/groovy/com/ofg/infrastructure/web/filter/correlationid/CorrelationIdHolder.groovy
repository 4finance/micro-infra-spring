package com.ofg.infrastructure.web.filter.correlationid

import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * Component that stores correlation id using {@link ThreadLocal}
 */
@TypeChecked
//from: https://github.com/daniel-bryant-uk/correlation-id-sync/
class CorrelationIdHolder {
    public static final String CORRELATION_ID_HEADER = "correlationId"
    private static final ThreadLocal<String> id = new ThreadLocal<String>()

    @PackageScope
    static void set(String correlationId) {
        id.set(correlationId)
    }

    static String get() {
        return id.get()
    }

    @PackageScope
    static void remove() {
        id.remove()
    }
}
