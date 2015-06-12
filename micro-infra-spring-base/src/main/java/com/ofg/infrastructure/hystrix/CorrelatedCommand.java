package com.ofg.infrastructure.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;

import java.util.concurrent.Callable;

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
        return CorrelationIdUpdater.withId(clientCorrelationId, new Callable<R>() {
            @Override
            public R call() throws Exception {
                return doRun();
            }

        });
    }

    public abstract R doRun() throws Exception;
}
