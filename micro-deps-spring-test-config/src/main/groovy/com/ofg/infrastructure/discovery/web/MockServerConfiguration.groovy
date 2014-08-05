package com.ofg.infrastructure.discovery.web

import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@TypeChecked
@Configuration
class MockServerConfiguration {

    @Bean(initMethod = 'start', destroyMethod = 'shutdownServer')
    HttpMockServer httpMockServer(@Value('${wiremock.port:8030}') Integer wiremockPort) {
        return new HttpMockServer(wiremockPort)
    }
    
}
