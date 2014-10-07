package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.ModelCamelContext

/**
 * This class is a setter of correlationId interception mechanism on all routes defined in {@link ModelCamelContext}.
 */
@Slf4j
@CompileStatic
class CorrelationIdOnCamelRouteSetter {

    private static final String ANY = '*'
    private final ModelCamelContext camelContext

    CorrelationIdOnCamelRouteSetter(ModelCamelContext camelContext) {
        this.camelContext = camelContext
    }

    /**
     * Adds {@link CorrelationIdInterceptor} as received/sent message interceptor in all route definitions from {@link ModelCamelContext}.
     */
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
