package com.ofg.infrastructure.camel

import groovy.transform.CompileStatic
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder

@CompileStatic
class CorrelationIdRouteBuilder extends RouteBuilder {

    @Override
    void configure() throws Exception {
        Processor correlationIdInterceptor = new CorrelationIdInterceptor()
        interceptFrom().process(correlationIdInterceptor)
        interceptSendToEndpoint("*").process(correlationIdInterceptor)
    }

}
