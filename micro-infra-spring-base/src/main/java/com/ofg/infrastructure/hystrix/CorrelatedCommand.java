package com.ofg.infrastructure.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import groovy.lang.Closure;

public abstract class CorrelatedCommand<R> extends HystrixCommand<R> {

    private final String clientCorrelationId = CorrelationIdHolder.get();

    protected CorrelatedCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected CorrelatedCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected final R run() throws Exception {
        return CorrelationIdUpdater.withId(clientCorrelationId, new Closure<R>(this, this) {
            public R doCall(Object it) throws Exception {
                return doRun();
            }
        });
    }

    public abstract R doRun() throws Exception;
}
