package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.camel.CamelContext
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder

@Slf4j
@CompileStatic
class CamelRouteModifier {

    private static final String ANY = '*'
    private final CamelContext camelContext

    CamelRouteModifier(CamelContext camelContext) {
        this.camelContext = camelContext
    }

    void addCorrelationIdInterception() {
        log.debug('Modifying routes defined in Camel context...')
        camelContext.getRouteDefinitions().each {
            it.adviceWith(camelContext, new RouteBuilder() {
                @Override
                void configure() throws Exception {
                    Processor correlationIdInterceptor = new CorrelationIdInterceptor()
                    interceptFrom().process(correlationIdInterceptor)
                    interceptSendToEndpoint(ANY).process(correlationIdInterceptor)
                }
            })
        }
    }

}
