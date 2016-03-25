package com.ofg.infrastructure.correlationid;

import org.springframework.cloud.sleuth.Span;

/**
 * Component that stores correlation id using {@link ThreadLocal}
 */
public class CorrelationIdHolder {
    public static final String CORRELATION_ID_HEADER = Span.TRACE_ID_NAME;
    public static final String OLD_CORRELATION_ID_HEADER = "correlationId";

}
