package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.watcher.DefaultDependencyPresenceOnStartupChecker
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@TypeChecked
@Configuration
@PackageScope
class ServiceResolverConfiguration {
        
    @PackageScope
    @Bean(initMethod = 'registerDependencies', destroyMethod = 'unregisterDependencies')
    DependencyWatcher dependencyWatcher(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        //TODO: Add a default listener that checks whether each dependency is online (default implementation treats all deps as critical)
        return new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, [:].withDefault { new DefaultDependencyPresenceOnStartupChecker() } )
    }
    
    @PackageScope
    @Bean
    ServiceConfigurationResolver serviceConfigurationResolver(@Value('${microservice.config.file:microservice.json}') ClassPathResource microserviceConfig) {
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
    ServiceInstance serviceInstance(@Value('${microservice.url:localhost}') String microserviceUrl,
                                    @Value('${microservice.port:8080}') int microservicePort,
                                    @Value('${microservice.context:rest}') String microserviceContext,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        return ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/$microserviceContext"))
                                        .address(microserviceUrl)
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
