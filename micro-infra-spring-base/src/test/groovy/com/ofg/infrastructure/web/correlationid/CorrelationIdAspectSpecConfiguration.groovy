package com.ofg.infrastructure.web.correlationid

import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@TypeChecked
@Configuration
class CorrelationIdAspectSpecConfiguration {
    @Bean
    CorrelationIdAspectSpec.AspectTestingController aspectTestingController(ServiceRestClient serviceRestClient,
                                                    HttpMockServer httpMockServer) {
        return new CorrelationIdAspectSpec.AspectTestingController(serviceRestClient, httpMockServer)
    }
}


