package com.ofg.infrastructure.web.resttemplate.fluent
import com.ofg.infrastructure.discovery.ServiceResolver
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations

/**
 * Creates a bean of abstraction over {@link RestOperations}.
 * 
 * @see ServiceRestClient
 * @see ServiceResolver
 */
@Configuration
@TypeChecked
class ServiceRestClientConfiguration {

    @Bean
    ServiceRestClient serviceRestClient(RestOperations restOperations, ServiceResolver serviceResolver) {
        return new ServiceRestClient(restOperations, serviceResolver)
    }

}
