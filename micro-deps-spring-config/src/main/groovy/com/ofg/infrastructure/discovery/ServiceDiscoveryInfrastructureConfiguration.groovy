package com.ofg.infrastructure.discovery

import com.ofg.config.BasicProfiles
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
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
import org.springframework.context.annotation.Profile

import static com.ofg.config.BasicProfiles.*

/**
 * Class holding configuration to Zookeeper server, Zookeeper service instance and to Curator framework.
 * 
 * All the beans are available only in the {@link BasicProfiles#PRODUCTION} profile.
 * 
 * @see CuratorFramework
 * @see ServiceInstance
 * @see ServiceDiscovery
 */
@CompileStatic
@Configuration
@Profile(PRODUCTION)
class ServiceDiscoveryInfrastructureConfiguration {
    
    @PackageScope
    @Bean(initMethod = 'start', destroyMethod = 'close')
    CuratorFramework curatorFramework(@Value('${service.resolver.url:localhost:2181}') String serviceResolverUrl,
                                      @Value('${service.resolver.connection.retry.times:5}') int numberOfRetries,
                                      @Value('${service.resolver.connection.retry.wait:1000}') int sleepMsBetweenRetries) {
        return CuratorFrameworkFactory.newClient(serviceResolverUrl, new RetryNTimes(numberOfRetries, sleepMsBetweenRetries))
    }    

    @PackageScope
    @Bean
    ServiceInstance serviceInstance(MicroserviceAddressProvider addressProvider,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        return ServiceInstance.builder().uriSpec(new UriSpec('{scheme}://{address}:{port}'))
                                        .address(addressProvider.host)
                                        .port(addressProvider.port)
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
}
