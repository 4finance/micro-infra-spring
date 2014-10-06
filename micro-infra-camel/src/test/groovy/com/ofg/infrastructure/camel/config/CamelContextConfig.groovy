package com.ofg.infrastructure.camel.config

import groovy.util.logging.Slf4j
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan('com.ofg.infrastructure.camel')
@Slf4j
class CamelContextConfig extends SingleRouteCamelConfiguration {

    @Bean
    @Override
    RouteBuilder route() {
        log.info('route builder creation')
        return new TestRouteBuilder()
    }
}
