package com.ofg.infrastructure.camel

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.springframework.cloud.sleuth.IdGenerator
import org.springframework.cloud.sleuth.Trace
import spock.lang.Specification

class CorrelationIdInterceptorSpec extends Specification {

    Trace trace = Stub()

    def 'should set new correlationId header in request inbound message if missing'() {
        given:
            Exchange exchange = defaultExchange()
        and:
            IdGenerator uuidGeneratorMock = Mock(IdGenerator)
            uuidGeneratorMock.create() >> '42'
        when:
            new CorrelationIdInterceptor(uuidGeneratorMock, trace).process(exchange)
        then:
            exchange.in.getHeader(Trace.TRACE_ID_NAME) == '42'
    }

    Exchange defaultExchange() {
        CamelContext context = new DefaultCamelContext()
        return new DefaultExchange(context)
    }
}
