package com.ofg.infrastructure.discovery;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that binds together whole service discovery
 */
@Import({DiscoveryPropertiesEnabler.class, MicroserviceJsonConfiguration.class, SpringCloudZookeeperConfiguration.class })
@Configuration
public class ServiceResolverConfiguration {

}
