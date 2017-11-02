package com.ofg.infrastructure.discovery

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.details.InstanceSerializer
import org.apache.curator.x.discovery.details.JsonInstanceSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.commons.util.InetUtils
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceDiscovery
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

import static com.ofg.config.BasicProfiles.SPRING_CLOUD
/**
 * Custom configuration that helps initialize deprecated ZookeeperServiceDiscovery and ZookeeperDiscoveryProperties
 */
@Profile(SPRING_CLOUD)
class SpringCloudZookeeperDiscoveryClientConfiguration extends ZookeeperDiscoveryClientConfiguration {

    @Autowired(required = false)
    private ZookeeperDependencies zookeeperDependencies

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
    @Primary
    ZookeeperDiscoveryClient zookeeperDiscoveryClient(ZookeeperServiceDiscovery zookeeperServiceDiscovery) {
        return new ZookeeperDiscoveryClient(zookeeperServiceDiscovery, this.zookeeperDependencies)
    }

    @Bean
    InstanceSerializer<ZookeeperInstance> instanceSerializer() {
        return new JsonInstanceSerializer<>(ZookeeperInstance.class)
    }
    
}
