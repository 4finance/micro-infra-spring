package com.ofg.infrastructure.discovery;

import org.springframework.cloud.util.UtilAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that binds together whole service discovery
 */
@Import({MicroserviceJsonConfiguration.class, SpringCloudZookeeperConfiguration.class, UtilAutoConfiguration.class })
@Configuration
public class ServiceResolverConfiguration {

}
