package com.ofg.infrastructure.discovery

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
import org.springframework.core.io.Resource

/**
 * Configuration of microservice's dependencies resolving classes. 
 */
@CompileStatic
@Configuration
class DependencyResolutionConfiguration {

    @Autowired(required = false) DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier

    @PackageScope
    @Bean(initMethod = 'registerDependencies', destroyMethod = 'unregisterDependencies')
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, dependencyPresenceOnStartupVerifier ?: new MissingDependencyLoggingOnStartupVerifier())
    }

    @PackageScope
    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:classpath:microservice.json}') Resource microserviceConfig) {
        return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
    }

    @Bean(initMethod = 'start', destroyMethod = 'close')
    ServiceResolver serviceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new ZookeeperServiceResolver(serviceConfigurationResolver, serviceDiscovery)
    }

}
