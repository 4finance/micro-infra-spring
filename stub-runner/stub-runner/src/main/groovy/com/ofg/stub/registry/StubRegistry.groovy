package com.ofg.stub.registry

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
        return ServiceDiscoveryBuilder.builder(Map)
                .basePath(stubServer.projectMetadata.context)
                .client(client)
                .thisInstance(serviceInstance)
                .build()
    }

    private static ServiceInstance serviceInstanceOf(StubServer stubServer) {
        return ServiceInstance.builder()
                .uriSpec(URI_SPEC)
                .address(resolveLocalhost())
                .port(stubServer.port)
                .name(serviceName(stubServer))
                .build()
    }

    private static String resolveLocalhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress()
        } catch (UnknownHostException e) {
            log.error('Exception occurred while trying to resolve localhost address', e)
            return 'localhost'
        }
    }

    private static String serviceName(StubServer stubServer) {
        String projectRelativePath = stubServer.projectMetadata.projectRelativePath
        String projectName = stubServer.projectMetadata.projectName
        if (projectRelativePath.endsWith(projectName)) {
            return projectRelativePath
        } else {
            return pathWithoutProjectName(projectRelativePath) + projectName
        }
    }

    private static String pathWithoutProjectName(String projectRelativePath) {
        if (projectRelativePath.contains('/')) {
            int idx = projectRelativePath.lastIndexOf('/')
            return projectRelativePath.substring(0, idx + 1)
        } else {
            return ''
        }
    }
}
