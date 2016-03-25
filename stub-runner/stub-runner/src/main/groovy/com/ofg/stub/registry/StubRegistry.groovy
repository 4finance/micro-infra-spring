package com.ofg.stub.registry

import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.server.StubServer
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

@CompileStatic
@Slf4j
class StubRegistry {
    private static final UriSpec URI_SPEC = new UriSpec('{scheme}://{address}:{port}')

    private final CuratorFramework client
    private final String connectionString

    StubRegistry(String connectionString, CuratorFramework client) {
        this.connectionString = connectionString
        this.client = client
    }

    void register(Collection<StubServer> stubServers) {
        stubServers.each { StubServer stubServer ->
            registerInstance(stubServer, client)
        }
    }

    String getConnectString() {
        return connectionString
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
                .name(serviceName(stubServer))
                .build()
    }

    private static String serviceName(StubServer stubServer) {
        ProjectMetadata metadata = stubServer.projectMetadata
        String projectRelativePath = metadata.projectRelativePath
        if (projectRelativePath.endsWith(metadata.projectName)) {
            return projectRelativePath
        } else {
            int idx = projectRelativePath.lastIndexOf('/')
            return projectRelativePath.substring(0, idx + 1) + metadata.projectName
        }
    }
}
