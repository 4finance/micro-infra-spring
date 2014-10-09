package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder

/**
 * This class is a setter of correlationId interception mechanism on all routes defined in route builders' beans.
 */
@Slf4j
@CompileStatic
class CorrelationIdOnCamelRouteSetter {

    private static final String ANY = '*'
    private final List<RouteBuilder> builders

    CorrelationIdOnCamelRouteSetter(List<RouteBuilder> builders) {
        this.builders = builders
    }

    /**
     * Adds {@link CorrelationIdInterceptor} as received/sent message interceptor in all routes defined in {@link RouteBuilder builders}.
     */
    void addCorrelationIdInterception() {
        Processor correlationIdInterceptor = new CorrelationIdInterceptor()
        builders.each {
            it.interceptFrom().process(correlationIdInterceptor)
            it.interceptSendToEndpoint(ANY).process(correlationIdInterceptor)
        }
    }


}
