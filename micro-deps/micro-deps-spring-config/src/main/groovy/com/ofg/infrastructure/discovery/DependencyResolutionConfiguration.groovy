package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.util.ProviderStrategyFactory
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

/**
 * Configuration of microservice's dependencies resolving classes.
 * 
 * <ul>
 *     <li>{@link DependencyWatcher} - checks if dependencies are online on microservice startup. Reacts with {@link DependencyPresenceOnStartupVerifier}</li>
 *     <li>{@link ZookeeperServiceResolver} - tries to connect to production Zookeeper</li>
 *     <li>{@link ServiceConfigurationResolver} - parses the provided microservice metadata</li>
 * </ul>
 * 
 * @see DependencyWatcher
 * @see ServiceResolver
 * @see ZookeeperServiceResolver
 * @see ServiceConfigurationResolver
 */
@CompileStatic
@Configuration
class DependencyResolutionConfiguration {

    @Autowired(required = false)
    private DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier

    @Autowired(required = false)
    private ProviderStrategyFactory providerStrategyFactory

    @PackageScope
    @Bean(initMethod = 'registerDependencies', destroyMethod = 'unregisterDependencies')
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, dependencyPresenceOnStartupVerifier ?: new DefaultDependencyPresenceOnStartupVerifier())
    }

    @Bean(initMethod = 'start', destroyMethod = 'close')
    ServiceResolver zooKeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery, CuratorFramework curatorFramework) {
        return new ZookeeperServiceResolver(serviceConfigurationResolver, serviceDiscovery, curatorFramework, providerStrategyFactory ?: new ProviderStrategyFactory())
    }

    @PackageScope
    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:classpath:microservice.json}') Resource microserviceConfig) {
        return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
    }

}
