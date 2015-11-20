package com.ofg.infrastructure.tracing;

import org.springframework.cloud.sleuth.TraceContextHolder;

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
