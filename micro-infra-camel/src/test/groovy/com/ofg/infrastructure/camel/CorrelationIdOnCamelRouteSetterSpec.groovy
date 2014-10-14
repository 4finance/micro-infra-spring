package com.ofg.infrastructure.camel

import com.ofg.infrastructure.camel.config.TestRouteBuilder
import groovy.util.logging.Slf4j
import org.apache.camel.CamelContext
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultRouteContext
import org.apache.camel.model.InterceptSendToEndpointDefinition
import org.apache.camel.model.OutputDefinition
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.processor.WrapProcessor
import org.apache.camel.spi.RouteContext
import spock.lang.Specification

@Slf4j
class CorrelationIdOnCamelRouteSetterSpec extends Specification {

    private String ANY_ENDPOINT_URI = '*'
    CamelContext camelContext
    TestRouteBuilder routeBuilder
    CorrelationIdOnCamelRouteSetter correlationIdOnCamelRouteSetter

    def setup() {
        routeBuilder = new TestRouteBuilder()
        camelContext = new DefaultCamelContext()
        correlationIdOnCamelRouteSetter = new CorrelationIdOnCamelRouteSetter([routeBuilder])
    }

    def 'should have correlationId interception set for input messages'() {
        given:
            RouteContext routeCtx = new DefaultRouteContext(camelContext)
        when:
            correlationIdOnCamelRouteSetter.addCorrelationIdInterception()
        then:
            routeBuilder.routeCollection.interceptFroms.find {
                interceptionOnCorrelationId(it, routeCtx)
            }
    }

    def 'should have correlationId interception set for output messages'() {
        given:
            RouteContext routeCtx = new DefaultRouteContext(camelContext)
        when:
            correlationIdOnCamelRouteSetter.addCorrelationIdInterception()
        then:
            routeBuilder.routeCollection.interceptSendTos.find {
                sendToInterceptionOnCorrelationId(it, routeCtx)
            }
    }

    private ProcessorDefinition<?> sendToInterceptionOnCorrelationId(InterceptSendToEndpointDefinition it, RouteContext routeCtx) {
        return interceptionOnAnyEndpoint(it) ? interceptionOnCorrelationId(it, routeCtx): null
    }

    private ProcessorDefinition<?> interceptionOnCorrelationId(OutputDefinition it, RouteContext routeCtx) {
        return it.outputs.find { processorContainingCorrelationIdInterception(it, routeCtx) }
    }

    private Processor processorContainingCorrelationIdInterception(ProcessorDefinition<?> it, RouteContext routeCtx) {
        Processor processor = it.createProcessor(routeCtx)
        if (processor instanceof WrapProcessor) {
            return processor.next().find { it instanceof CorrelationIdInterceptor }
        } else {
            return null
        }
    }

    private boolean interceptionOnAnyEndpoint(InterceptSendToEndpointDefinition it) {
        it.uri == ANY_ENDPOINT_URI
    }
}
