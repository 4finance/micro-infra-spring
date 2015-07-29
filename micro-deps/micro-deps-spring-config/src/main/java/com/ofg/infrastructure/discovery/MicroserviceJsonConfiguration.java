package com.ofg.infrastructure.discovery;

import com.ofg.config.NotSpringCloudProfile;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Deprecated
@NotSpringCloudProfile
@Import({AddressProviderConfiguration.class, ServiceDiscoveryInfrastructureConfiguration.class, DependencyResolutionConfiguration.class})
@AutoConfigureBefore(ZookeeperAutoConfiguration.class)
public class MicroserviceJsonConfiguration {
}
