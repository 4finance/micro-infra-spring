package com.ofg.stub.registry

import com.ofg.stub.server.StubServer
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

@TypeChecked
@Slf4j
class StubRegistry {
    private static final UriSpec URI_SPEC = new UriSpec('{scheme}://{address}:{port}')
    private static final RetryPolicy RETRY_POLICY = new RetryNTimes(50, 100)

    private final TestingServer zookeeperServer

    StubRegistry(int port) {
        zookeeperServer = new TestingServer(port)
    }

    void register(Collection<StubServer> stubServers) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperServer.connectString, RETRY_POLICY)
        client.start()
        stubServers.each {
            registerInstance(it, client)
        }
    }

    void shutdown() {
        zookeeperServer.close()
    }

    int getPort() {
        return zookeeperServer.port
    }

    private static void registerInstance(StubServer stubServer, CuratorFramework client) {
        ServiceDiscovery serviceDiscovery = serviceDiscoveryFor(stubServer, client)
        serviceDiscovery.start()
        log.debug("Registered stub server for project ${stubServer.projectMetadata.projectRelativePath} in ${stubServer.projectMetadata.context} context")
    }

    @PackageScope
    static ServiceDiscovery serviceDiscoveryFor(StubServer stubServer, CuratorFramework client) {
        ServiceInstance serviceInstance = serviceInstanceOf(stubServer)
        return ServiceDiscoveryBuilder.builder(Void)
                .basePath(stubServer.projectMetadata.context)
                .client(client)
                .thisInstance(serviceInstance)
                .build()
    }

    private static ServiceInstance serviceInstanceOf(StubServer stubServer) {
        return ServiceInstance.builder()
                .uriSpec(URI_SPEC)
                .address('localhost')
                .port(stubServer.port)
                .name(stubServer.projectMetadata.projectRelativePath)
                .build()
    }
}
