package com.ofg.infrastructure.tracing
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContextHolder
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SpanRemovingRunnableSpec extends Specification {

    def 'should remove span from thread local after finishing work'() {
        given:
            ExecutorService executor = Executors.newSingleThreadExecutor()
        and:
            Span initialSpan = Stub()
            Span firstSpan
            Runnable runnable =  {
                TraceContextHolder.currentSpan = initialSpan
                firstSpan = TraceContextHolder.currentSpan
            }
        and:
            executor.submit(new SpanRemovingCallable<Span>(runnable)).get()
        expect:
            firstSpan != null
        and:
            Span secondSpan
            Runnable secondRunnable =  {
                secondSpan = TraceContextHolder.currentSpan
            }
        when:
            executor.submit(new SpanRemovingRunnable(secondRunnable)).get()
        then:
            secondSpan == null
    }
}
