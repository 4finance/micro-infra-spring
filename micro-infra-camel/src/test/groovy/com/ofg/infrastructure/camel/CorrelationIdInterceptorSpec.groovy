package com.ofg.infrastructure.camel

import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import spock.lang.Specification

class CorrelationIdInterceptorSpec extends Specification {

    def "should set new correlationId header in request inbound message if missing"() {
        given:
            Exchange exchange = defaultExchange()
        when:
            new CorrelationIdInterceptor().process(exchange)
        then:
            exchange.getIn().getHeader(CorrelationIdHolder.CORRELATION_ID_HEADER) != null
    }

    def "should set correlationId in holder from header of inbound message"() {
        given:
            Exchange exchange = defaultExchange()
            def correlationIdValue = UUID.randomUUID().toString()
            exchange.getIn().setHeader(CorrelationIdHolder.CORRELATION_ID_HEADER, correlationIdValue)
        when:
            new CorrelationIdInterceptor().process(exchange)
        then:
            CorrelationIdHolder.get() == correlationIdValue
    }

    Exchange defaultExchange() {
        CamelContext context = new DefaultCamelContext()
        return new DefaultExchange(context)
    }
}
