package com.ofg.infrastructure.base
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import spock.lang.Specification

class SpecWithZookeper extends Specification {

    TestingServer server
    ServiceConfigurationResolver serviceConfigurationResolver
    ServiceInstance<Void> serviceInstance
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
        serviceConfigurationResolver = new ServiceConfigurationResolver(Samples.MICROSERVICE_CONFIG)
        serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/"))
                .address('anyUrl')
                .port(10)
                .name(this.serviceConfigurationResolver.microserviceName)
                .build()
        curatorFramework = CuratorFrameworkFactory.newClient(server.connectString, new RetryNTimes(5, 500))
        curatorFramework.start()
        serviceDiscovery = ServiceDiscoveryBuilder.builder(Void)
                .basePath(this.serviceConfigurationResolver.basePath)
                .client(curatorFramework)
                .thisInstance(this.serviceInstance)
                .build()
        serviceDiscovery.start()
    }


    def cleanup() {
        server.close()
        curatorFramework.close()
        serviceDiscovery.close()
    }
    
}
