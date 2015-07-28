package com.ofg.infrastructure.discovery;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * This configuration imports configurations related to service discovery
 *
 * @see ServiceResolverConfiguration
 */
@Configuration
@Import(ServiceResolverConfiguration.class)
public class ServiceDiscoveryConfiguration {
}
