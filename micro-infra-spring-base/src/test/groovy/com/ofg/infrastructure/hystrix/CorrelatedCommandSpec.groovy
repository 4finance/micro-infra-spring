package com.ofg.infrastructure.hystrix

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater
import org.springframework.cloud.sleuth.MilliSpan
import spock.lang.Specification

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey


class CorrelatedCommandSpec extends Specification {

    public static final String CORRELATION_ID = 'A'

    def 'should run Hystrix command with client correlation ID'() {
        given:
            CorrelationIdUpdater.updateCorrelationId(MilliSpan.builder().traceId(CORRELATION_ID).build())
            def command = new CorrelatedCommand<String>(withGroupKey(asKey(""))) {
                String doRun() {
                    return CorrelationIdHolder.get().traceId
                }
            }

        when:
            def result = command.execute()

        then:
            result == CORRELATION_ID
    }

    def 'should run Hystrix command in different thread'() {
        given:
            def command = new CorrelatedCommand<String>(withGroupKey(asKey(""))) {
                String doRun() {
                    return Thread.currentThread().name
                }
            }
        when:
            def threadName = command.execute()

        then:
            Thread.currentThread().name != threadName
    }

}
