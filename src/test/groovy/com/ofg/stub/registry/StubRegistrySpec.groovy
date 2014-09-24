package com.ofg.stub.registry

import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.server.StubServer
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.ServiceProvider
import spock.lang.AutoCleanup
import spock.lang.Specification

class StubRegistrySpec extends Specification {
    static final int STUB_REGISTRY_PORT = 12181
    static final int HELLO_STUB_SERVER_PORT = 12182
    static final int BYE_STUB_SERVER_PORT = 12183
    static final ProjectMetadata HELLO_STUB_METADATA = new ProjectMetadata('hello', 'com/ofg/hello', 'pl')
    static final ProjectMetadata BYE_STUB_METADATA = new ProjectMetadata('bye', 'com/ofg/bye', 'pl')

    @AutoCleanup('stop') StubServer helloStub = new StubServer(HELLO_STUB_SERVER_PORT, HELLO_STUB_METADATA, [])
    @AutoCleanup('stop') StubServer byeStub = new StubServer(BYE_STUB_SERVER_PORT, BYE_STUB_METADATA, [])
    @AutoCleanup('shutdown') StubRegistry stubRegistry = new StubRegistry(STUB_REGISTRY_PORT)

    def 'should register stub servers'() {
        given:
            helloStub.start()
            byeStub.start()

        when:
            stubRegistry.register([helloStub, byeStub])

        then:
            with(resolveStubServerInstanceFromRegistry(helloStub)) {
                name == HELLO_STUB_METADATA.projectRelativePath
                address == 'localhost'
                port == HELLO_STUB_SERVER_PORT
            }
        and:
            with(resolveStubServerInstanceFromRegistry(byeStub)) {
                name == BYE_STUB_METADATA.projectRelativePath
                address == 'localhost'
                port == BYE_STUB_SERVER_PORT
            }
    }

    private ServiceInstance resolveStubServerInstanceFromRegistry(StubServer stubServer) {
        CuratorFrameworkFactory.newClient("localhost:$STUB_REGISTRY_PORT", new RetryNTimes(5, 10)).withCloseable {
            it.start()
            ServiceDiscovery serviceDiscovery = StubRegistry.serviceDiscoveryFor(stubServer, it)
            ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(stubServer.projectMetadata.projectRelativePath).build()
            serviceProvider.start()
            return serviceProvider.instance
        }
    }
}
