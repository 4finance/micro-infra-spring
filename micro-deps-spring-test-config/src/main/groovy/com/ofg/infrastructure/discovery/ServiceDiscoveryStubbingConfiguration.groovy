package com.ofg.infrastructure.discovery

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
class ServiceDiscoveryStubbingConfiguration {

    @Bean
    ServiceResolver serviceResolver(@Value('${wiremock.port:8030}') Integer wiremockPort,
                                    @Value('${wiremock.url:localhost}') String wiremockUrl,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        ServiceResolver serviceResolver = new StubbedServiceResolver(wiremockPort, wiremockUrl)
        serviceResolver.stubDependenciesFrom(serviceConfigurationResolver)
        return serviceResolver
    }

    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:classpath:microservice.json}') Resource microserviceConfig) {
        return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
    }

}
