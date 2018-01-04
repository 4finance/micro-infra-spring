package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistry;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistryAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * Configuration that binds together whole service discovery.
 */
@Import(ConsumerDrivenContractConfiguration.class)
@ImportAutoConfiguration({
        ZookeeperDiscoveryClientConfiguration.class,
        ZookeeperDiscoveryAutoConfiguration.class,
        ZookeeperServiceRegistryAutoConfiguration.class,
        ZookeeperAutoServiceRegistrationAutoConfiguration.class,
        ZookeeperAutoConfiguration.class,
        UtilAutoConfiguration.class
})
@Configuration
@Profile(BasicProfiles.SPRING_CLOUD)
public class ZookeeperServiceResolverConfiguration {

    @Autowired Environment environment;
    @Autowired ApplicationContext applicationContext;

    @Bean
    public ServiceResolver zooKeeperServiceResolver(ZookeeperDependencies zookeeperDependencies,
                                                    DiscoveryClient discoveryClient,
                                                    ZookeeperServiceRegistry zookeeperServiceRegistry,
                                                    CuratorFramework curatorFramework,
                                                    ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        return new SpringCloudZookeeperServiceResolver(zookeeperDependencies,
                discoveryClient, curatorFramework, zookeeperServiceRegistry, zookeeperDiscoveryProperties);
    }

}
