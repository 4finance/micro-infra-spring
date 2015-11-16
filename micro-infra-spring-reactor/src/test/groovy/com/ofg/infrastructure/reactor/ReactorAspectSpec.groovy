package com.ofg.infrastructure.reactor

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.reactor.event.ReactorEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.cloud.sleuth.Span
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import reactor.core.Reactor
import reactor.event.Event
import reactor.spring.annotation.Selector
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference

import static com.jayway.awaitility.Awaitility.await
import static org.hamcrest.Matchers.equalTo

@Ignore
@ContextConfiguration(classes = Config, loader = SpringApplicationContextLoader)
class ReactorAspectSpec extends Specification {

    public static final String EXPECTED_CORRELATION_ID = '121231231231'

    @Autowired MySubscriber mySubscriber
    @Autowired MySender mySender

    def 'should set correlation id on an event'() {
        given:
            correlationIdIsSet()
        when:
            mySender.sendsEvent()
        then:
            correlationIdShouldBeSetFromSentEventHeader()
    }

    void correlationIdIsSet() {
        CorrelationIdHolder.set(EXPECTED_CORRELATION_ID)
    }

    private String correlationIdShouldBeSetFromSentEventHeader() {
        await().untilAtomic(mySubscriber.atomicReference, equalTo(EXPECTED_CORRELATION_ID))
    }

    @Configuration
    @ComponentScan
    @EnableAutoConfiguration
    static class Config {

    }


    @Component
    static class MySubscriber {
        @Autowired Reactor reactor

        AtomicReference<Span> atomicReference = new AtomicReference<>()

        @Selector('key')
        void receive(Event<String> event) {
            atomicReference.set(CorrelationIdHolder.get())
        }

        Reactor getReactor() {
            return reactor
        }
    }

    @Component
    static class MySender {
        @Autowired
        public Reactor reactor

        void sendsEvent() {
            reactor.notify('key', ReactorEvent.wrap('data'))
        }
    }
}
