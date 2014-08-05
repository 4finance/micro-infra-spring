package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.MissingDependencyLoggingOnStartupVerifier
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

/**
 * Configuration of microservice's dependencies resolving classes. 
 */
@TypeChecked
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

    @Bean(initMethod = 'startServiceProviders', destroyMethod = 'stopServiceProviders')
    ServiceResolver serviceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new ServiceResolver(serviceConfigurationResolver, serviceDiscovery)
    }

}
