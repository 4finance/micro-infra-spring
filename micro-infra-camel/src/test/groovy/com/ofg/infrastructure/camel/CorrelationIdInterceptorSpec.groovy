package com.ofg.infrastructure.camel
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.springframework.cloud.sleuth.IdGenerator
import org.springframework.cloud.sleuth.Trace
import spock.lang.Specification

class CorrelationIdInterceptorSpec extends Specification {

    def 'should set new correlationId header in request inbound message if missing'() {
        given:
            Exchange exchange = defaultExchange()
        and:
            IdGenerator uuidGeneratorMock = Mock(IdGenerator)
            uuidGeneratorMock.create() >> '42'
        when:
            new CorrelationIdInterceptor(uuidGeneratorMock).process(exchange)
        then:
            exchange.in.getHeader(Trace.TRACE_ID_NAME) == '42'
    }

    def 'should set correlationId in holder from header of inbound message'() {
        given:
            Exchange exchange = defaultExchange()
            def correlationIdValue = UUID.randomUUID().toString()
            exchange.in.setHeader(Trace.TRACE_ID_NAME, correlationIdValue)
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
