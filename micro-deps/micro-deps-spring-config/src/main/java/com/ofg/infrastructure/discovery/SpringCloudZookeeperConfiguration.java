package com.ofg.infrastructure.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ ZookeeperServiceResolverConfiguration.class, SpringCloudZookeeperConnectorConfiguration.class})
public class SpringCloudZookeeperConfiguration {

    @Bean
    public ServiceResolver zooKeeperServiceResolver(ZookeeperDependencies zookeeperDependencies,
                                                    DiscoveryClient discoveryClient,
                                                    ServiceDiscovery serviceDiscovery,
                                                    CuratorFramework curatorFramework,
                                                    ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        return new SpringCloudZookeeperServiceResolver(zookeeperDependencies, discoveryClient, curatorFramework, serviceDiscovery, zookeeperDiscoveryProperties);
    }
}
