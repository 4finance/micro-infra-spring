package com.ofg.infrastructure.discovery
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.FailOnMissingDependencyOnStartupVerifier
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@TypeChecked
@Configuration
class ServiceResolverConfiguration {
            
    @Autowired(required = false) DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier
    
    @PackageScope
    @Bean(initMethod = 'registerDependencies', destroyMethod = 'unregisterDependencies')
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, dependencyPresenceOnStartupVerifier ?: new FailOnMissingDependencyOnStartupVerifier())
    }
    
    @PackageScope
    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:classpath:microservice.json}') Resource microserviceConfig) {
        return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
    }
    
    
    @PackageScope
    @Bean(initMethod = 'start', destroyMethod = 'close')
    CuratorFramework curatorFramework(@Value('${service.resolver.url:localhost:2181}') String serviceResolverUrl,
                                      @Value('${service.resolver.connection.retries:5}') int numberOfRetries,
                                      @Value('${service.resolver.connection.timeout:1000}') int timeout) {
        return CuratorFrameworkFactory.newClient(serviceResolverUrl, new RetryNTimes(numberOfRetries, timeout))
    }    
    
    
    @PackageScope
    @Bean
    ServiceInstance serviceInstance(@Value('${microservice.host:localhost}') String microserviceHost,
                                    @Value('${microservice.port:8080}') int microservicePort,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        return ServiceInstance.builder().uriSpec(new UriSpec('{scheme}://{address}:{port}'))
                                        .address(microserviceHost)
                                        .port(microservicePort)
                                        .name(serviceConfigurationResolver.microserviceName)
                                        .build()
    }
    
    @PackageScope
    @Bean(initMethod = 'start', destroyMethod = 'close')
    ServiceDiscovery serviceDiscovery(CuratorFramework curatorFramework, 
                                      ServiceInstance serviceInstance,
                                      ServiceConfigurationResolver serviceConfigurationResolver) {
        return ServiceDiscoveryBuilder.builder(Void).basePath(serviceConfigurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build()
    }    
    

    @Bean(initMethod = 'startServiceProviders', destroyMethod = 'stopServiceProviders')
    ServiceResolver serviceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        return new ServiceResolver(serviceConfigurationResolver, serviceDiscovery)
    }
    
}
