package com.ofg.stub.registry

import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.server.StubServer
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.ServiceProvider
import spock.lang.Specification

class StubRegistrySpec extends Specification {
    static final int STUB_REGISTRY_PORT = 12181
    static final int HELLO_STUB_SERVER_PORT = 12182
    static final int BYE_STUB_SERVER_PORT = 12182
    static final ProjectMetadata HELLO_STUB_METADATA = new ProjectMetadata('com/ofg/ping', 'pl')
    static final ProjectMetadata BYE_STUB_METADATA = new ProjectMetadata('com/ofg/ping', 'pl')

    StubRegistry stubRegistry = new StubRegistry(STUB_REGISTRY_PORT)

    def 'should register stub servers'() {
        given:
            StubServer helloStub = new StubServer(HELLO_STUB_SERVER_PORT, HELLO_STUB_METADATA, [])
            StubServer byeStub = new StubServer(BYE_STUB_SERVER_PORT, BYE_STUB_METADATA, [])

        when:
            stubRegistry.register([helloStub, byeStub])

        then:
            ServiceInstance helloStubInstance = resolveStubServerInstanceFromRegistry(helloStub)
            helloStubInstance.name == HELLO_STUB_METADATA.projectName
            helloStubInstance.address == 'localhost'
            helloStubInstance.port == HELLO_STUB_SERVER_PORT
        and:
            ServiceInstance byeStubInstance = resolveStubServerInstanceFromRegistry(byeStub)
            byeStubInstance.name == BYE_STUB_METADATA.projectName
            byeStubInstance.address == 'localhost'
            byeStubInstance.port == BYE_STUB_SERVER_PORT
    }

    def cleanup() {
        stubRegistry.shutdown()
    }

    private ServiceInstance resolveStubServerInstanceFromRegistry(StubServer stubServer) {
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:$STUB_REGISTRY_PORT", new RetryNTimes(5, 10))
        try {
            client.start()
            ServiceDiscovery serviceDiscovery = StubRegistry.serviceDiscoveryFor(stubServer, client)
            ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(stubServer.projectMetadata.projectName).build()
            serviceProvider.start()
            return serviceProvider.instance
        } finally {
            client.close()
        }
    }
}
