package com.ofg.stub.registry

import com.ofg.stub.server.StubServer
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

@CompileStatic
@Slf4j
class StubRegistry {
    private static final UriSpec URI_SPEC = new UriSpec('{scheme}://{address}:{port}')
    private static final RetryPolicy RETRY_POLICY = new RetryNTimes(50, 100)

    private TestingServer zookeeperServer
    private String localZookeeperPath

    StubRegistry(int port) {
        this.zookeeperServer = new TestingServer(port)
    }

    StubRegistry(String localZookeeperPath) {
        this.localZookeeperPath = localZookeeperPath
    }

    StubRegistry(TestingServer testingServer) {
        this.zookeeperServer = testingServer
    }

    void register(Collection<StubServer> stubServers) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(getConnectString(), RETRY_POLICY)
        client.start()
        stubServers.each { StubServer stubServer ->
            registerInstance(stubServer, client)
        }
    }

    String getConnectString() {
        String connectString = zookeeperServer ? zookeeperServer.connectString : localZookeeperPath
        if (StringUtils.isBlank(connectString)) {
            throw new IllegalArgumentException('You have to provide either Zookeeper port or a path to a local Zookeeper')
        }
        return connectString
    }

    void shutdown() {
        zookeeperServer.close()
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
