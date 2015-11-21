package com.ofg.infrastructure.tracing;

import org.springframework.cloud.sleuth.TraceContextHolder;

/**
 * Workaround for the fact that after completing a task a span is not removed from the Thread Local
 * TODO: Remove after fixing in Sleuth
 *
 */
public class SpanRemovingRunnable implements Runnable {

    private final Runnable delegate;

    public SpanRemovingRunnable(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            delegate.run();
        } finally {
            TraceContextHolder.removeCurrentSpan();
        }
    }
}
