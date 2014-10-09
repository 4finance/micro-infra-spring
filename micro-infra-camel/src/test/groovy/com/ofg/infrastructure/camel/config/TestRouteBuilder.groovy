package com.ofg.infrastructure.camel.config

import groovy.transform.CompileStatic
import org.apache.camel.builder.RouteBuilder

@CompileStatic
class TestRouteBuilder extends RouteBuilder {

    @Override
    void configure() throws Exception {
        log.info('Route builder bean configuration...')
        from('direct:start').to('mock:result').routeId('route-1')
    }

}