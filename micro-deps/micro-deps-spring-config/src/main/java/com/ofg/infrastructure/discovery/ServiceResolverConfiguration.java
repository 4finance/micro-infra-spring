package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that binds together whole service discovery. Imports:
 * <p/>
 * <ul>
 * <li>{@link AddressProviderConfiguration} - contains beans related to microservice's address and port resolution</li>
 * <li>{@link ServiceDiscoveryInfrastructureConfiguration} - contains beans related to connection to service discovery provider (available only in {@link BasicProfiles#PRODUCTION}</li>
 * <li>{@link DependencyResolutionConfiguration} - Configuration of microservice's dependencies resolving classes.
 * </ul>
 */
@Import({AddressProviderConfiguration.class, ServiceDiscoveryInfrastructureConfiguration.class, DependencyResolutionConfiguration.class })
@Configuration
public class ServiceResolverConfiguration {
}
