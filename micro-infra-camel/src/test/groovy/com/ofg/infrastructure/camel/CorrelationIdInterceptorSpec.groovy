package com.ofg.infrastructure.camel

import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import com.ofg.infrastructure.correlationid.UuidGenerator
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.springframework.cloud.sleuth.IdGenerator
import spock.lang.Specification

class CorrelationIdInterceptorSpec extends Specification {

    def 'should set new correlationId header in request inbound message if missing'() {
        given:
            Exchange exchange = defaultExchange()
        and:
            UuidGenerator uuidGeneratorMock = Mock(UuidGenerator)
            uuidGeneratorMock.create() >> '42'
        when:
            new CorrelationIdInterceptor(uuidGeneratorMock).process(exchange)
        then:
            exchange.in.getHeader(CorrelationIdHolder.CORRELATION_ID_HEADER) == '42'
    }

    def 'should set correlationId in holder from header of inbound message'() {
        given:
            Exchange exchange = defaultExchange()
            def correlationIdValue = UUID.randomUUID().toString()
            exchange.in.setHeader(CorrelationIdHolder.CORRELATION_ID_HEADER, correlationIdValue)
        when:
            new CorrelationIdInterceptor(Stub(IdGenerator)).process(exchange)
        then:
            CorrelationIdHolder.get().traceId == correlationIdValue
    }

    Exchange defaultExchange() {
        CamelContext context = new DefaultCamelContext()
        return new DefaultExchange(context)
    }
}
