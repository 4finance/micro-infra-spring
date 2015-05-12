package com.ofg.infrastructure.hystrix

import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.correlationid.CorrelationIdUpdaterUtil
import groovy.transform.CompileStatic

@CompileStatic
abstract class CorrelatedCommand<R> extends HystrixCommand<R> {

    private final String clientCorrelationId = CorrelationIdHolder.get()

    protected CorrelatedCommand(HystrixCommandGroupKey group) {
        super(group)
    }

    protected CorrelatedCommand(HystrixCommand.Setter setter) {
        super(setter)
    }

    @Override
    protected final R run() throws Exception {
        return CorrelationIdUpdaterUtil.withId(clientCorrelationId) {
            doRun()
        }
    }

    abstract R doRun() throws Exception
}
