package com.ofg.stub.mapping

import com.ofg.infrastructure.discovery.InstanceDetails
import com.ofg.stub.server.ZookeeperServer
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceProvider

@Slf4j
class ProjectMetadataResolver {

    static Collection<ProjectMetadata> resolveFromZookeeper(String serviceName, String context, ZookeeperServer zookeeperServer) {
        List<String> dependencies = resolveServiceDependenciesFromZookeeper(context, zookeeperServer, serviceName)
        Set<ProjectMetadata> projects = []
        dependencies.each {
            projects << new ProjectMetadata(serviceName, it, context)
        }
        return projects
    }

    private static List<String> resolveServiceDependenciesFromZookeeper(String context, ZookeeperServer zookeeperServer, String serviceName) {
        ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(InstanceDetails)
                .basePath(context)
                .client(zookeeperServer.curatorFramework)
                .build()
        discovery.start()
        ServiceProvider serviceProvider = discovery.serviceProviderBuilder().serviceName(serviceName).build()
        serviceProvider.start()
        List<String> dependencies = serviceProvider.getInstance().payload.dependencies
        serviceProvider?.close()
        discovery?.close()
        return dependencies
    }

}
