package com.ofg.infrastructure.discovery;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceDiscovery;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.SocketUtils;

import com.ofg.config.BasicProfiles;

/**
 * Configuration that binds together whole service discovery. Imports:
 * <p/>
 * <ul>
 * <li>{@link AddressProviderConfiguration} - contains beans related to microservice's address and port resolution</li>
 * <li>{@link ServiceDiscoveryInfrastructureConfiguration} - contains beans related to connection to service discovery provider (available only in {@link BasicProfiles#PRODUCTION}</li>
 * <li>{@link DependencyResolutionConfiguration} - Configuration of microservice's dependencies resolving classes.
 * </ul>
 */
@Import({ConsumerDrivenContractConfiguration.class, ZookeeperAutoConfiguration.class, UtilAutoConfiguration.class})
@Configuration
@Profile(BasicProfiles.SPRING_CLOUD)
public class ZookeeperServiceResolverConfiguration {

    static final Integer DEFAULT_SERVER_PORT = 8080;

    @Autowired Environment environment;
    @Autowired ApplicationContext applicationContext;

    @Bean(initMethod = "build")
    public ZookeeperServiceDiscovery zookeeperServiceDiscovery(CuratorFramework curator, ZookeeperDiscoveryProperties zookeeperDiscoveryProperties, InstanceSerializer<ZookeeperInstance> instanceSerializer) {
        ZookeeperServiceDiscovery zookeeperServiceDiscovery = new ZookeeperServiceDiscovery(curator, zookeeperDiscoveryProperties, instanceSerializer);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(zookeeperServiceDiscovery);
        zookeeperServiceDiscovery.setPort(resolveMicroservicePort(environment));
        return zookeeperServiceDiscovery;
    }

    @Bean
    public ServiceResolver zooKeeperServiceResolver(ZookeeperDependencies zookeeperDependencies,
                                                    DiscoveryClient discoveryClient,
                                                    ZookeeperServiceDiscovery zookeeperServiceDiscovery,
                                                    CuratorFramework curatorFramework,
                                                    ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        return new SpringCloudZookeeperServiceResolver(zookeeperDependencies,
                discoveryClient, curatorFramework, zookeeperServiceDiscovery, zookeeperDiscoveryProperties);
    }

    private Integer resolveMicroservicePort(Environment environment) {
        final String property = System.getProperty("port");
        String port = StringUtils.isNotBlank(property) ? property : environment.getProperty("server.port");
        return port != null ? randomizePortIfRequired(port) : DEFAULT_SERVER_PORT;
    }

    private Integer randomizePortIfRequired(String port) {
        Integer portToPick = Integer.valueOf(port);
        if (portToPick <= 0) {
            return SocketUtils.findAvailableTcpPort();
        }
        return portToPick;
    }
}
