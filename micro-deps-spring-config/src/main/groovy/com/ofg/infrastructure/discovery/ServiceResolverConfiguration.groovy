package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@CompileStatic
@Import([AddressProviderConfiguration, ServiceDiscoveryInfrastructureConfiguration, DependencyResolutionConfiguration])
@Configuration
class ServiceResolverConfiguration { }
