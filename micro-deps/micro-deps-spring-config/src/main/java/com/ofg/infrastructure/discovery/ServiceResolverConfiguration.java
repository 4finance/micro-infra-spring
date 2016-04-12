package com.ofg.infrastructure.discovery;

import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.cloud.sleuth.log.SleuthLogAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that binds together whole service discovery
 */
@Import({MicroserviceJsonConfiguration.class, SpringCloudZookeeperConfiguration.class, UtilAutoConfiguration.class, SleuthLogAutoConfiguration.class })
@Configuration
public class ServiceResolverConfiguration {

}
