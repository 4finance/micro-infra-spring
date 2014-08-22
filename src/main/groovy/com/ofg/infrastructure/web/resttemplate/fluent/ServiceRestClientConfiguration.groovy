package com.ofg.infrastructure.web.resttemplate.fluent
import com.ofg.infrastructure.discovery.ServiceResolver
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@TypeChecked
class ServiceRestClientConfiguration {

    @Bean
    ServiceRestClient serviceRestClient(RestTemplate restTemplate, ServiceResolver serviceResolver) {
        return new ServiceRestClient(restTemplate, serviceResolver)
    }

}
