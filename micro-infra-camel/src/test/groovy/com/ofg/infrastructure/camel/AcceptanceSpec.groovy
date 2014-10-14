package com.ofg.infrastructure.camel

import com.ofg.infrastructure.camel.config.CamelRouteAsBeanConfiguration
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import spock.lang.AutoCleanup

import groovy.util.logging.Slf4j
import org.apache.camel.ProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultProducerTemplate
import org.apache.camel.impl.InterceptSendToEndpoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER

@Slf4j
@ContextConfiguration(classes = [CamelRouteAsBeanConfiguration.class])
class AcceptanceSpec extends Specification {

    @Autowired ModelCamelContext camelContext
    @AutoCleanup('stop') ProducerTemplate template
    MockEndpoint resultEndpoint

    def setup() {
        resultEndpoint =
                ((InterceptSendToEndpoint)camelContext.getEndpoint('mock:result')).getDelegate()
        template = new DefaultProducerTemplate(
                camelContext,
                camelContext.getEndpoint('direct:start'))
        template.start()
    }

    def cleanup() {
        removeRouteDefinitions()
    }

    def 'should set correlationId from header of input message'() {
        given:
            String correlationIdValue = UUID.randomUUID().toString()
        when:
            template.sendBodyAndHeader('<message/>', CORRELATION_ID_HEADER, correlationIdValue)
        then:
            CorrelationIdHolder.get() == correlationIdValue
    }

    def 'should set new correlationId if header in input message is empty'() {
        when:
            template.sendBody('<message/>')
        then:
            CorrelationIdHolder.get() != null
    }

    def 'should set correlationId in output message when it is missing on the input'() {
        when:
            template.sendBody('<message/>')
        then:
            resultEndpoint.message(0).header(CORRELATION_ID_HEADER).isNotNull()
    }

    def 'should copy correlationId from header of input message to the output'() {
        given:
            String correlationIdValue = UUID.randomUUID().toString()
        when:
            template.sendBodyAndHeader('<message/>', CORRELATION_ID_HEADER, correlationIdValue)
        then:
            resultEndpoint.message(0).header(CORRELATION_ID_HEADER).isEqualTo(correlationIdValue)
    }

    private void removeRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = camelContext.routeDefinitions
        camelContext.removeRouteDefinitions(routeDefinitions)
    }
}
