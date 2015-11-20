package com.ofg.infrastructure.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.TraceContextHolder;
import org.springframework.cloud.sleuth.TraceScope;
import org.springframework.cloud.sleuth.instrument.circuitbreaker.TraceCommand;

/**
 * HystrixCommand that stores information about the span from the current thread
 *
 * TODO: Remove after fixing in Sleuth
 *
 * @param <R>
 */
public abstract class CorrelatedCommand<R> extends TraceCommand<R> {

    private final Trace trace;
    private final Span storedSpan;

    protected CorrelatedCommand(Trace trace, HystrixCommandGroupKey group) {
        super(trace, group);
        this.trace = trace;
        storedSpan = TraceContextHolder.getCurrentSpan();
    }

    protected CorrelatedCommand(Trace trace, HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
        super(trace, group, threadPool);
        this.trace = trace;
        storedSpan = TraceContextHolder.getCurrentSpan();
    }

    protected CorrelatedCommand(Trace trace, HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
        super(trace, group, executionIsolationThreadTimeoutInMilliseconds);
        this.trace = trace;
        storedSpan = TraceContextHolder.getCurrentSpan();
    }

    protected CorrelatedCommand(Trace trace, HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
        super(trace, group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
        this.trace = trace;
        storedSpan = TraceContextHolder.getCurrentSpan();
    }

    protected CorrelatedCommand(Trace trace, Setter setter) {
        super(trace, setter);
        this.trace = trace;
        storedSpan = TraceContextHolder.getCurrentSpan();
    }

    @Override
    protected R run() throws Exception {
        TraceScope scope = trace.startSpan(getCommandKey().name(), storedSpan);
        try {
            return doRun();
        } finally {
            scope.close();
            TraceContextHolder.removeCurrentSpan();
        }
    }

}
