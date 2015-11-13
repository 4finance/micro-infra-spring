package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependenciesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({ SpringCloudZookeeperConnectorConfiguration.class, ZookeeperServiceResolverConfiguration.class, ZookeeperDependenciesAutoConfiguration.class})
@Profile(BasicProfiles.SPRING_CLOUD)
@AutoConfigureBefore(ZookeeperAutoConfiguration.class)
public class SpringCloudZookeeperConfiguration {

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework(ZookeeperConnector zookeeperConnector, RetryPolicy retryPolicy) {
        return CuratorFrameworkFactory.newClient(zookeeperConnector.getServiceResolverUrl(), retryPolicy);
    }

    @Bean
    public SpringCloudToMicroserviceJsonConverter springCloudToMicroserviceJsonConverter(@Value("${spring.application.name}") String basePath,
                                                                                         ZookeeperDependencies zookeeperDependencies,
                                                                                         ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        return new SpringCloudToMicroserviceJsonConverter(basePath, zookeeperDependencies, zookeeperDiscoveryProperties);
    }

}
