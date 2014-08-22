package com.ofg.infrastructure.discovery

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@TypeChecked
@Configuration
@Import(ServiceResolverConfiguration)
class ServiceDiscoveryConfiguration {
}
