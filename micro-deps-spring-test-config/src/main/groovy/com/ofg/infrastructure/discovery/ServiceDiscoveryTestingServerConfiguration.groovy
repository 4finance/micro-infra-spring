package com.ofg.infrastructure.discovery

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

@Configuration
class ServiceDiscoveryTestingServerConfiguration {

    @Value('${service.resolver.url:localhost:2181}') String serviceResolverUrl
    @Value('${wiremock.port:8030}') Integer wiremockPort
    @Value('${wiremock.url:localhost}') String wiremockUrl
    @Autowired CuratorFramework curatorFramework
    @Autowired ServiceConfigurationResolver serviceConfigurationResolver
    @Autowired TestingServer testingServer
    
    @Bean(destroyMethod = 'close')
    TestingServer testingServer() {
        return new TestingServer(serviceResolverUrl.split(':').last().toInteger())
    }

    @PostConstruct
    void setupStubs() {
        serviceConfigurationResolver.dependencies.each {          
            ServiceInstance<Void> serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/${it.key}"))
                    .address(wiremockUrl)
                    .port(wiremockPort)
                    .name(it.value)
                    .build()
            ServiceDiscoveryBuilder.builder(Void).basePath(serviceConfigurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build().start()
        }

    }

}
