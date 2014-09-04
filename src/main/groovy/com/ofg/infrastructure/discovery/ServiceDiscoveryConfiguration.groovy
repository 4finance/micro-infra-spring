package com.ofg.infrastructure.discovery
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
/**
 * This configuration imports configurations related to service discovery
 *
 * @see ServiceResolverConfiguration
 */
@CompileStatic
@Configuration
@Import(ServiceResolverConfiguration)
class ServiceDiscoveryConfiguration {
}
