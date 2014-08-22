package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.discovery.ServiceResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ServiceRestClientConfiguration {

    @Bean
    public ServiceRestClient serviceRestClient(RestTemplate restTemplate, ServiceResolver serviceResolver) {
        return new ServiceRestClient(restTemplate, serviceResolver)
    }

}
