package com.ofg.stub.config

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.details.InstanceSerializer
import org.apache.curator.x.discovery.details.JsonInstanceSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.commons.util.InetUtils
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceDiscovery
import org.springframework.context.annotation.Bean

/**
 * Custom configuration that helps initialize deprecated ZookeeperServiceDiscovery and ZookeeperDiscoveryProperties
 */
class CustomZookeeperDiscoveryClientConfiguration extends ZookeeperDiscoveryClientConfiguration {

    @Autowired
    private CuratorFramework curator

    @Bean
    @ConditionalOnMissingBean
    ZookeeperDiscoveryProperties zookeeperDiscoveryProperties(InetUtils inetUtils) {
        return new ZookeeperDiscoveryProperties(inetUtils)
    }

    @Bean
    @ConditionalOnMissingBean
    ZookeeperServiceDiscovery zookeeperServiceDiscovery(ZookeeperDiscoveryProperties zookeeperDiscoveryProperties, 
                                                               InstanceSerializer<ZookeeperInstance> instanceSerializer) {
        return new ZookeeperServiceDiscovery(this.curator, zookeeperDiscoveryProperties, instanceSerializer)
    }

    @Bean
    InstanceSerializer<ZookeeperInstance> instanceSerializer() {
        return new JsonInstanceSerializer<>(ZookeeperInstance.class)
    }
    
}
