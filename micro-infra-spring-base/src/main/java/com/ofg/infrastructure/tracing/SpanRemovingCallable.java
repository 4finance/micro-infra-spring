package com.ofg.infrastructure.tracing;

import org.springframework.cloud.sleuth.TraceContextHolder;

import java.util.concurrent.Callable;

/**
 * Workaround for the fact that after completing a task a span is not removed from the Thread Local
 * TODO: Remove after fixing in Sleuth
 *
 * @param <T>
 */
public class SpanRemovingCallable<T> implements Callable<T> {

    private final Callable<T> delegate;

    public SpanRemovingCallable(Callable<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T call() throws Exception {
        try {
            return delegate.call();
        } finally {
            TraceContextHolder.removeCurrentSpan();
        }
    }
}
