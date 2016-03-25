package com.ofg.infrastructure.camel

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.springframework.cloud.sleuth.Tracer
import spock.lang.Specification

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

class CorrelationIdInterceptorSpec extends Specification {

    Tracer trace = Stub(Tracer)

    def 'should set new correlationId header in request inbound message if missing'() {
        given:
            Exchange exchange = defaultExchange()
        and:
            Random uuidGeneratorMock = Stub(Random)
            uuidGeneratorMock.nextLong() >> 42
        when:
            new CorrelationIdInterceptor(uuidGeneratorMock, trace).process(exchange)
        then:
            exchange.in.getHeader(CORRELATION_ID_HEADER) == 42
    }

    Exchange defaultExchange() {
        CamelContext context = new DefaultCamelContext()
        return new DefaultExchange(context)
    }
}
