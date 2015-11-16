package com.ofg.infrastructure.correlationid;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContextHolder;

/**
 * Component that stores correlation id using {@link ThreadLocal}
 */
public class CorrelationIdHolder {
    public static final String CORRELATION_ID_HEADER = "correlationId";

    public static void set(Span span) {
        TraceContextHolder.setCurrentSpan(span);
    }

    public static Span get() {
        return TraceContextHolder.getCurrentSpan();
    }

    public static void remove() {
        TraceContextHolder.removeCurrentSpan();
    }
}
