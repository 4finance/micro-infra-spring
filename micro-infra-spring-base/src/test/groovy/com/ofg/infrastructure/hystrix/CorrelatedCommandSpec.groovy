package com.ofg.infrastructure.hystrix

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import org.springframework.cloud.sleuth.MilliSpan
import org.springframework.cloud.sleuth.RandomUuidGenerator
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.Trace
import org.springframework.cloud.sleuth.TraceScope
import org.springframework.cloud.sleuth.sampler.IsTracingSampler
import org.springframework.cloud.sleuth.trace.DefaultTrace
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey


class CorrelatedCommandSpec extends Specification {

    public static final String CORRELATION_ID = 'A'
    ApplicationEventPublisher applicationEventPublisher = Stub()
    Trace trace = new DefaultTrace(new IsTracingSampler(), new RandomUuidGenerator(), applicationEventPublisher)

    def 'should run Hystrix command with client correlation ID'() {
        given:
            Span span = MilliSpan.builder().traceId(CORRELATION_ID).build()
            CorrelationIdUpdater.updateCorrelationId(span)
            TraceScope traceScope = trace.startSpan("test", span)

        and:
            def command = new CorrelatedCommand<String>(trace, withGroupKey(asKey(""))) {
                String doRun() {
                    return CorrelationIdHolder.get()?.traceId
                }
            }

        when:
            def result = command.execute()

        then:
            result == CORRELATION_ID

        cleanup:
            traceScope.close()
            CorrelationIdHolder.remove()
    }

    def 'should run Hystrix command in different thread'() {
        given:
            Span span = MilliSpan.builder().traceId(CORRELATION_ID).build()
            CorrelationIdUpdater.updateCorrelationId(span)
            TraceScope traceScope = trace.startSpan("test", span)

        and:
            def command = new CorrelatedCommand<String>(trace, withGroupKey(asKey(""))) {
                String doRun() {
                    return Thread.currentThread().name
                }
            }
        when:
            def threadName = command.execute()

        then:
            Thread.currentThread().name != threadName

        cleanup:
            traceScope.close()
    }

}
