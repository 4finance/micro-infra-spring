package com.ofg.infrastructure.base

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import spock.lang.Specification

import static com.ofg.infrastructure.discovery.MicroserviceConfigurationUtil.CONFIGURATION_WITH_PATH_ELEM

class SpecWithZookeper extends Specification {

    TestingServer server
    ServiceConfigurationResolver serviceConfigurationResolver
    ServiceInstance<Map> serviceInstance
    CuratorFramework curatorFramework
    ServiceDiscovery serviceDiscovery
    
    def setup() {
        setupTestingServer()
        setupZookeeper(server)
    }

    private void setupTestingServer() {
        server = new TestingServer()
    }

    private void setupZookeeper(TestingServer server) {
        serviceConfigurationResolver = new ServiceConfigurationResolver(serviceConfig())
        serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/"))
                .address('anyUrl')
                .port(10)
                .name(serviceConfigurationResolver.microservicePath.path)
                .build()
        curatorFramework = CuratorFrameworkFactory.newClient(server.connectString, new ExponentialBackoffRetry(20, 20, 500))
        curatorFramework.start()
        serviceDiscovery = ServiceDiscoveryBuilder.builder()
                .basePath(this.serviceConfigurationResolver.basePath)
                .client(curatorFramework)
                .thisInstance(this.serviceInstance)
                .build()
        serviceDiscovery.start()
    }

    String serviceConfig() {
        return CONFIGURATION_WITH_PATH_ELEM
    }

    def cleanup() {
        server.close()
        curatorFramework.close()
        serviceDiscovery.close()
    }
    
}
