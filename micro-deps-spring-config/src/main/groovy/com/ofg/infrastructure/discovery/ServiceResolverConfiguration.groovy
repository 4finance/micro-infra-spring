package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration that binds together whole service discovery. Imports:
 *
 * <ul>
 *     <li>{@link AddressProviderConfiguration} - contains beans related to microservice's address and port resolution</li>
 *     <li>{@link ServiceDiscoveryInfrastructureConfiguration} - contains beans related to connection to service discovery provider (available only in {@link com.ofg.loans.config.BasicProfiles#PRODUCTION}</li>
 *     <li>{@link DependencyResolutionConfiguration} - Configuration of microservice's dependencies resolving classes.
 * </ul>
 * 
 */
@CompileStatic
@Import([AddressProviderConfiguration, ServiceDiscoveryInfrastructureConfiguration, DependencyResolutionConfiguration])
@Configuration
class ServiceResolverConfiguration { }
