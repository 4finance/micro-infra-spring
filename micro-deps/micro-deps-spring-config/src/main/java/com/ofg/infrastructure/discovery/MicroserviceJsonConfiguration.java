package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Deprecated
@Profile("!" + BasicProfiles.SPRING_CLOUD)
@Import({AddressProviderConfiguration.class, ServiceDiscoveryInfrastructureConfiguration.class, DependencyResolutionConfiguration.class})
@AutoConfigureBefore(ZookeeperAutoConfiguration.class)
public class MicroserviceJsonConfiguration {
}
