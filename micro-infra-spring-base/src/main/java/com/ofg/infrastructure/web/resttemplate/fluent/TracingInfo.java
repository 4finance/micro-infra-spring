package com.ofg.infrastructure.web.resttemplate.fluent;

import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Wrapper class to easy pass through constructors.
 * Adds tracing capabilities to fluent api.
 */
public class TracingInfo {

    private final Tracer tracer;
    private final TraceKeys traceKeys;

    public TracingInfo(Tracer tracer, TraceKeys traceKeys) {
        this.tracer = tracer;
        this.traceKeys = traceKeys;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public TraceKeys getTraceKeys() {
        return traceKeys;
    }
}
