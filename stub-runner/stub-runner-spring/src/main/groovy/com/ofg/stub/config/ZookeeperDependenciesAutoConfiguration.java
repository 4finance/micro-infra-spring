package com.ofg.stub.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  microservice without dependencies fix
 *  
 *  https://github.com/spring-cloud/spring-cloud-zookeeper/issues/74
 */
@Configuration
@EnableConfigurationProperties
@AutoConfigureAfter(ZookeeperAutoConfiguration.class)
public class ZookeeperDependenciesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperDependencies zookeeperDependencies() {
        return new ZookeeperDependencies();
    }
}
