package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.apache.curator.test.TestingServer;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(BasicProfiles.SPRING_CLOUD)
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
