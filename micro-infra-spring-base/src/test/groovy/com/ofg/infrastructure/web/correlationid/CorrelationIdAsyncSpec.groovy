package com.ofg.infrastructure.web.correlationid

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.trace.SpanContextHolder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.test.context.ContextConfiguration
import spock.util.concurrent.PollingConditions

import java.util.concurrent.atomic.AtomicReference

@ContextConfiguration(classes = [BaseConfiguration, CorrelationIdAsyncSpecConfiguration], loader = SpringApplicationContextLoader)
class CorrelationIdAsyncSpec extends MicroserviceMvcWiremockSpec {

    @Autowired AsyncClass asyncClass
    @Autowired AsyncDelegation asyncDelegation
    @Autowired Random idGenerator

    PollingConditions pollingConditions = new PollingConditions()

    def "should set correlationId on an async annotated method"() {
        given:
            Span span = Span.builder().traceId(idGenerator.nextLong()).spanId(idGenerator.nextLong()).build()
            SpanContextHolder.currentSpan = span
        when:
            asyncDelegation.doSthSync()
        then:
            pollingConditions.eventually {
                assert span.traceId == asyncClass.span?.get()?.traceId
                assert span.name != asyncClass.span?.get()?.name
            }
        cleanup:
            SpanContextHolder.removeCurrentSpan()
    }

    @CompileStatic
    @Configuration
    @EnableAsync
    static class CorrelationIdAsyncSpecConfiguration {

        @Bean AsyncClass asyncClass() {
            return new AsyncClass()
        }

        @Bean AsyncDelegation asyncDelegation() {
            return new AsyncDelegation(asyncClass())
        }
    }

    static class AsyncDelegation {

        private final AsyncClass asyncClass

        AsyncDelegation(AsyncClass asyncClass) {
            this.asyncClass = asyncClass
        }

        void doSthSync() {
            asyncClass.doSth()
        }
    }

    static class AsyncClass {

        AtomicReference<Span> span

        @Async
        void doSth() {
            span = new AtomicReference<>(SpanContextHolder.currentSpan)
        }
    }
}
