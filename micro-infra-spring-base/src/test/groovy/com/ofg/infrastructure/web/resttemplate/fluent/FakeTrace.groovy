package com.ofg.infrastructure.web.resttemplate.fluent

import org.mockito.Mockito
import org.springframework.cloud.sleuth.Sampler
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.Tracer

import java.util.concurrent.Callable

class FakeTrace implements Tracer {

    @Override
    Span createSpan(String name) {
        return Mockito.mock(Span)
    }

    @Override
    Span createSpan(String name, Span parent) {
        return Mockito.mock(Span)
    }

    @Override
    Span createSpan(String name, Sampler sampler) {
        return Mockito.mock(Span)
    }

    @Override
    Span continueSpan(Span span) {
        return Mockito.mock(Span)
    }

    @Override
    void addTag(String key, String value) {

    }

    @Override
    Span detach(Span span) {
        return Mockito.mock(Span)
    }

    @Override
    Span close(Span span) {
        return Mockito.mock(Span)
    }

    @Override
    def <V> Callable<V> wrap(Callable<V> callable) {
        return callable
    }

    @Override
    Runnable wrap(Runnable runnable) {
        return runnable
    }

    @Override
    Span getCurrentSpan() {
        return Mockito.mock(Span)
    }

    @Override
    boolean isTracing() {
        return true
    }
}
