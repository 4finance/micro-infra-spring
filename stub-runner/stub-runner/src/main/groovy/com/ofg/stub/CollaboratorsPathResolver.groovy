package com.ofg.stub

import com.ofg.stub.server.ZookeeperServer
import groovy.transform.InheritConstructors
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.ServiceProvider

/**
 * Class that resolves your service's collaborators from Zookeeper
 */
@Slf4j
@PackageScope
class CollaboratorsPathResolver {

    private static final String COLLABORATORS_ENDPOINT = '/collaborators'
    private static final String APPLICATION_JSON_CONTENT_TYPE = 'application/json'

    /**
     * Resolves the service's collaborators from Zookeeper
     *
     * @param serviceName - the name of the service for which you would like to grab your dependencies (e.g. 'com/ofg/foo/bar')
     * @param context - the realm in which the service is registered (e.g. 'pl')
     * @param zookeeperServer - the ZookeeperServer where your service is registered
     * @return - collaborators of your service
     */
    static Collaborators resolveFromZookeeper(String serviceName, String context, ZookeeperServer zookeeperServer) {
        List<String> dependencies = resolveServiceDependenciesFromZookeeper(context, zookeeperServer, serviceName)
        return new Collaborators(context, dependencies)
    }

    private static List<String> resolveServiceDependenciesFromZookeeper(String context, ZookeeperServer zookeeperServer, String serviceName) {
        ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(Void)
                .basePath(context)
                .client(zookeeperServer.curatorFramework)
                .build()
        discovery.start()
        ServiceProvider serviceProvider = discovery.serviceProviderBuilder().serviceName(serviceName).build()
        serviceProvider.start()
        ServiceInstance<Void> instance = serviceProvider.getInstance()
        if(!instance) {
            throw new LookedUpServiceUnavailableException("The service [$serviceName] in realm [$context] is unavailable - please check if's up and running")
        }
        String uriSpec = instance.buildUriSpec()
        Map<String, Map<String, String>> collaborators = new HTTPBuilder(uriSpec).get(path: COLLABORATORS_ENDPOINT, contentType: APPLICATION_JSON_CONTENT_TYPE)
        serviceProvider?.close()
        discovery?.close()
        return collaborators?.keySet()?.toList()
    }

    @InheritConstructors
    static class LookedUpServiceUnavailableException extends RuntimeException {

    }

}
