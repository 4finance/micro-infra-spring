package com.ofg.infrastructure.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CamelContextConfig extends SingleRouteCamelConfiguration {

    @Bean
    @Override
    RouteBuilder route() {
        return new TestRouteBuilder()
    }

    private class TestRouteBuilder extends CorrelationIdRouteBuilder {
        public void configure() {
            super.configure()
            from("direct:start").to("mock:result").routeId("route-1");
        }
    }
}
