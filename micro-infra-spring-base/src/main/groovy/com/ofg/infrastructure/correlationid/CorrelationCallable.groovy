package com.ofg.infrastructure.correlationid

import groovy.transform.CompileStatic

import java.util.concurrent.Callable

/**
 * A callable that wraps execution of a given {@link Callable} with correlation id.
 * 
 * This class solves the issue of async servlets that execute logic in a separate thread. Since
 * we are using {@link ThreadLocal} to contain correlation id for the current thread we would
 * loose this value when new thread is spawn. This class passes in its {@link Callable#call()} method
 * correlation id and sets it in the new thread's {@link ThreadLocal}
 * 
 * @param < T >
 *
 * @see CorrelationIdHolder
 * @see ThreadLocal
 */
@CompileStatic
class CorrelationCallable<T> implements Callable<T> {
    private String correlationId
    private Callable<T> callable

    CorrelationCallable(Callable<T> targetCallable) {
        correlationId = CorrelationIdHolder.get()
        callable = targetCallable
    }

    @Override
    T call() throws Exception {
        CorrelationIdHolder.set(correlationId)
        return callable.call()
    }

    static Callable<T> withCorrelationId(Closure closure) {
        return new CorrelationCallable(closure)
    }
}