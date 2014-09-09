package com.ofg.infrastructure.discovery

import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.MissingDependencyLoggingOnStartupVerifier
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.curator.x.discovery.ServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource

import static com.ofg.config.BasicProfiles.*

/**
 * Configuration of microservice's dependencies resolving classes.
 * 
 * <ul>
 *     <li>{@link DependencyWatcher} - checks if dependencies are online on microservice startup. Reacts with {@link DependencyPresenceOnStartupVerifier}</li>
 *     <li>{@link ZookeeperServiceResolver} - on {@link BasicProfiles#PRODUCTION} tries to connect to production Zookeeper</li>
 *     <li>{@link StubbedServiceResolver} - on {@link BasicProfiles#DEVELOPMENT} or {@link BasicProfiles#TEST} - creates a stubbed {@link ServiceResolver}</li>
 *     <li>{@link ServiceConfigurationResolver} - parses the provided microservice metadata</li>
 * </ul>
 * 
 * @see DependencyWatcher
 * @see ServiceResolver
 * @see ZookeeperServiceResolver
 * @see StubbedServiceResolver
 * @see ServiceConfigurationResolver
 */
@CompileStatic
@Configuration
class DependencyResolutionConfiguration {

    @Autowired(required = false) DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier

    @PackageScope
    @Bean(initMethod = 'registerDependencies', destroyMethod = 'unregisterDependencies')
    @Profile(PRODUCTION)
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, dependencyPresenceOnStartupVerifier ?: new MissingDependencyLoggingOnStartupVerifier())
    }

    @Bean(initMethod = 'start', destroyMethod = 'close')
    @Profile(PRODUCTION)
    ServiceResolver zooKeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new ZookeeperServiceResolver(serviceConfigurationResolver, serviceDiscovery)
    }

    @Bean
    @Profile([DEVELOPMENT, TEST])
    ServiceResolver stubbedServiceResolver(@Value('${wiremock.port:8030}') Integer wiremockPort,
                                           @Value('${wiremock.url:localhost}') String wiremockUrl,
                                           ServiceConfigurationResolver serviceConfigurationResolver) {
        ServiceResolver serviceResolver = new StubbedServiceResolver(wiremockPort, wiremockUrl)
        serviceResolver.stubDependenciesFrom(serviceConfigurationResolver)
        return serviceResolver
    }

    @PackageScope
    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:classpath:microservice.json}') Resource microserviceConfig) {
        return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
    }

}
