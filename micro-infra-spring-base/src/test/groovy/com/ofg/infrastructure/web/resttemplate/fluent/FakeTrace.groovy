package com.ofg.infrastructure.web.resttemplate.fluent

import org.mockito.Mockito
import org.springframework.cloud.sleuth.Sampler
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.Trace
import org.springframework.cloud.sleuth.TraceScope

import java.util.concurrent.Callable

class FakeTrace implements Trace {

    @Override
    TraceScope startSpan(String name) {
        return Mockito.mock(TraceScope)
    }

    @Override
    TraceScope startSpan(String name, Span parent) {
        return Mockito.mock(TraceScope)
    }

    @Override
    def <T> TraceScope startSpan(String name, Sampler<T> sampler, T info) {
        return Mockito.mock(TraceScope)
    }

    @Override
    TraceScope continueSpan(Span s) {
        return Mockito.mock(TraceScope)
    }

    @Override
    void addAnnotation(String key, String value) {

    }

    @Override
    def <V> Callable<V> wrap(Callable<V> callable) {
        return callable
    }

    @Override
    Runnable wrap(Runnable runnable) {
        return runnable
    }
}
