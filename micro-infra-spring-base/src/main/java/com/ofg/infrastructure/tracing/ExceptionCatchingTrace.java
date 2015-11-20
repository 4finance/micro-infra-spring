package com.ofg.infrastructure.tracing;

import org.springframework.cloud.sleuth.IdGenerator;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceScope;
import org.springframework.cloud.sleuth.trace.DefaultTrace;
import org.springframework.context.ApplicationEventPublisher;

/**
 * TODO: Remove after fixing in Sleuth
 */
public class ExceptionCatchingTrace extends DefaultTrace {
    public ExceptionCatchingTrace(Sampler<Void> defaultSampler, IdGenerator idGenerator, ApplicationEventPublisher publisher) {
        super(defaultSampler, idGenerator, publisher);
    }

    @Override
    public TraceScope continueSpan(Span span) {
        return new ExceptionCatchingTraceScope(super.continueSpan(span));
    }

    @Override
    public <T> TraceScope startSpan(String name, Sampler<T> s, T info) {
        return new ExceptionCatchingTraceScope(super.startSpan(name, s, info));
    }

    @Override
    public TraceScope startSpan(String name) {
        return new ExceptionCatchingTraceScope(super.startSpan(name));
    }

    @Override
    public TraceScope startSpan(String name, Span parent) {
        return new ExceptionCatchingTraceScope(super.startSpan(name, parent));
    }
}
