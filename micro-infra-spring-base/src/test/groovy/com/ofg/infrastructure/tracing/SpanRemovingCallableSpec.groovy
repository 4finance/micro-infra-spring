package com.ofg.infrastructure.tracing

import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContextHolder
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SpanRemovingCallableSpec extends Specification {

    def 'should remove span from thread local after finishing work'() {
        given:
            ExecutorService executor = Executors.newSingleThreadExecutor()
        and:
            Span initialSpan = Stub()
            Callable<Span> callable =  {
                TraceContextHolder.currentSpan = initialSpan
                return TraceContextHolder.currentSpan
            }
        and:
            executor.submit(new SpanRemovingCallable<Span>(callable)).get()
        and:
            Callable<Span> secondCallable =  {
                return TraceContextHolder.currentSpan
            }
        when:
            Span secondSpan = executor.submit(new SpanRemovingCallable<Span>(secondCallable)).get()
        then:
            secondSpan == null
    }
}
