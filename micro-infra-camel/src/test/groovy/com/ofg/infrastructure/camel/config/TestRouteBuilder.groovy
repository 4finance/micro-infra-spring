package com.ofg.infrastructure.camel.config;

import org.apache.camel.builder.RouteBuilder;

class TestRouteBuilder extends RouteBuilder {

    public void configure() {
        log.info('route builder configuration')
        from('direct:start').to('mock:result').routeId('route-1')
    }

}