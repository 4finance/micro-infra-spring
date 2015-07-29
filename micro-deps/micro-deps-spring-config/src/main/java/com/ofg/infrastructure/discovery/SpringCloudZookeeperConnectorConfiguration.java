package com.ofg.infrastructure.discovery;

import org.apache.curator.test.TestingServer;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudZookeeperConnectorConfiguration {

    @Bean
    @Conditional(ZookeeperConnectorConditions.StandaloneZookeeperCondition.class)
    public ZookeeperConnector standaloneZookeeperConnector(final ZookeeperProperties zookeeperProperties) {
        return new ZookeeperConnector() {
            @Override
            public String getServiceResolverUrl() {
                return zookeeperProperties.getConnectString();
            }

        };
    }

    @Bean
    @Conditional(ZookeeperConnectorConditions.InMemoryZookeeperCondition.class)
    public ZookeeperConnector inMemoryZookeeperConnector(final TestingServer testingServer) {
        return new ZookeeperConnector() {
            @Override
            public String getServiceResolverUrl() {
                return testingServer.getConnectString();
            }

        };
    }
}
