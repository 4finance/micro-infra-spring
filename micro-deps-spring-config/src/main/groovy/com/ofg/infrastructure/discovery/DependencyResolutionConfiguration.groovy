package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.MissingDependencyLoggingOnStartupVerifier
import com.ofg.loans.config.BasicProfiles
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.curator.x.discovery.ServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
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
    @Profile(BasicProfiles.PRODUCTION)
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, dependencyPresenceOnStartupVerifier ?: new MissingDependencyLoggingOnStartupVerifier())
    }

    @Bean(initMethod = 'start', destroyMethod = 'close')
    @Profile(BasicProfiles.PRODUCTION)
    ServiceResolver zooKeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new ZookeeperServiceResolver(serviceConfigurationResolver, serviceDiscovery)
    }

    @Bean
    @Profile([BasicProfiles.DEVELOPMENT, BasicProfiles.TEST])
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
