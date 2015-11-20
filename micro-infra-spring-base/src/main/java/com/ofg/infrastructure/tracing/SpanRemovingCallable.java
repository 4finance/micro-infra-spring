package com.ofg.infrastructure.tracing;

import org.springframework.cloud.sleuth.TraceContextHolder;

import java.util.concurrent.Callable;

public class SpanRemovingCallable implements Callable {

    private final Callable delegate;

    public SpanRemovingCallable(Callable delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object call() throws Exception {
        try {
            return delegate.call();
        } finally {
            TraceContextHolder.removeCurrentSpan();
        }
    }
}
