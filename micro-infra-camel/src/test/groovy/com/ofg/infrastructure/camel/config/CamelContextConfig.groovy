package com.ofg.infrastructure.camel.config

import groovy.util.logging.Slf4j
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration
import org.springframework.context.annotation.Configuration

@Slf4j
@Configuration
class CamelContextConfig extends SingleRouteCamelConfiguration {

    @Override
    RouteBuilder route() {
        log.info('route builder creation')
        return new TestRouteBuilder()
    }

}
