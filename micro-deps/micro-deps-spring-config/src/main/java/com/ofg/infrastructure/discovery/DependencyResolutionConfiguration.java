package com.ofg.infrastructure.discovery;

import com.ofg.config.NotSpringCloudProfile;
import com.ofg.infrastructure.discovery.util.ProviderStrategyFactory;
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher;
import com.ofg.infrastructure.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier;
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier;
import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Configuration of microservice's dependencies resolving classes.
 * <p/>
 * <ul>
 * <li>{@link DependencyWatcher} - checks if dependencies are online on microservice startup. Reacts with {@link DependencyPresenceOnStartupVerifier}</li>
 * <li>{@link ZookeeperServiceResolver} - tries to connect to production Zookeeper</li>
 * <li>{@link ServiceConfigurationResolver} - parses the provided microservice metadata</li>
 * </ul>
 *
 * @see DependencyWatcher
 * @see ServiceResolver
 * @see ZookeeperServiceResolver
 * @see ServiceConfigurationResolver
 */
@Configuration
@Deprecated
@NotSpringCloudProfile
public class DependencyResolutionConfiguration {

    @Autowired(required = false) DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier;
    @Autowired(required = false) ProviderStrategyFactory providerStrategyFactory;

    @Bean(initMethod = "registerDependencies", destroyMethod = "unregisterDependencies")
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        final DependencyPresenceOnStartupVerifier verifier = dependencyPresenceOnStartupVerifier;
        return new DependencyWatcher(serviceConfigurationResolver.getDependencies(), serviceDiscovery, verifier != null ? verifier : new DefaultDependencyPresenceOnStartupVerifier());
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public ServiceResolver zooKeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery, CuratorFramework curatorFramework) {
        final ProviderStrategyFactory factory = providerStrategyFactory;
        return new ZookeeperServiceResolver(serviceConfigurationResolver, serviceDiscovery, curatorFramework, factory != null ? factory : new ProviderStrategyFactory());
    }

    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value("${microservice.config.file:classpath:microservice.json}") Resource microserviceConfig) throws IOException {
        return new ServiceConfigurationResolver(IOUtils.toString(microserviceConfig.getInputStream()));
    }
}
