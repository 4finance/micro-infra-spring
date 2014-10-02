package com.ofg.infrastructure.camel

import org.apache.camel.builder.RouteBuilder

class CorrelationIdRouteBuilder extends RouteBuilder {

    @Override
    void configure() throws Exception {
        def correlationIdInterceptor = new CorrelationIdInterceptor()
        interceptFrom().process(correlationIdInterceptor)
        interceptSendToEndpoint("*").process(correlationIdInterceptor)
    }

}
