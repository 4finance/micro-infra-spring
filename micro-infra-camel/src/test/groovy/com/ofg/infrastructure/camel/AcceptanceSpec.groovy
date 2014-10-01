package com.ofg.infrastructure.camel

import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder
import org.springframework.test.annotation.DirtiesContext

import static com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder.*
import groovy.util.logging.Slf4j
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultProducerTemplate
import org.apache.camel.impl.InterceptSendToEndpoint
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.spring.javaconfig.test.JavaConfigContextLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(
        locations = "com.ofg.infrastructure.camel.CamelContextConfig",
        loader = JavaConfigContextLoader.class)
@Slf4j
class AcceptanceSpec extends Specification {

    @Autowired
    private ModelCamelContext camelContext

    @Autowired
    private RouteBuilder routeBuilder

    private MockEndpoint resultEndpoint;
    private ProducerTemplate template;

    def setup() {
        camelContext.addRoutes(routeBuilder)
        resultEndpoint = ((InterceptSendToEndpoint)camelContext.getEndpoint("mock:result")).getDelegate()
        template = new DefaultProducerTemplate(
                camelContext,
                camelContext.getEndpoint("direct:start"))
        template.start()
    }

    def cleanup() {
        template?.stop()
        removeRouteDefinitions()
    }

    @DirtiesContext
    def "should set correlationId from header of input message"() {
        given:
        String correlationIdValue = UUID.randomUUID().toString();

        when:
        template.sendBodyAndHeader("<message/>", CORRELATION_ID_HEADER, correlationIdValue);

        then:
        CorrelationIdHolder.get() == correlationIdValue
    }

    @DirtiesContext
    def "should set new correlationId if header in input message is empty"() {
        when:
        template.sendBody("<message/>");

        then:
        CorrelationIdHolder.get() != null
    }

    @DirtiesContext
    def "should set correlationId in output message when it is missing on the input"() {
        when:
        template.sendBody("<message/>");

        then:
        resultEndpoint.message(0).header(CORRELATION_ID_HEADER).isNotNull()
    }

    @DirtiesContext
    def "should copy correlationId from header of input message to the output"() {
        given:
        String correlationIdValue = UUID.randomUUID().toString();

        when:
        template.sendBodyAndHeader("<message/>", CORRELATION_ID_HEADER, correlationIdValue);

        then:
        resultEndpoint.message(0).header(CORRELATION_ID_HEADER).isEqualTo(correlationIdValue)
    }

    private void removeRouteDefinitions() {
        def routeDefinitions = camelContext.getRouteDefinitions()
        camelContext.removeRouteDefinitions(routeDefinitions)
    }
}
