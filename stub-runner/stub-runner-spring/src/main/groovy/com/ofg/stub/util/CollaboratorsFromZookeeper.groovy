package com.ofg.stub.util

import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.stub.Collaborators
import groovy.transform.CompileStatic
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependency

@CompileStatic
class CollaboratorsFromZookeeper {


    public static Collaborators fromZookeeperDependencies(ZookeeperDiscoveryProperties zookeeperDiscoveryProperties, ZookeeperDependencies zookeeperDependencies) {
        return new Collaborators(zookeeperDiscoveryProperties.root, getServicePathFromZookeeper(zookeeperDependencies), getStubsFromZookeeper(zookeeperDependencies))
    }

    private static List<String> getServicePathFromZookeeper(ZookeeperDependencies zookeeperDependencies) {
        return zookeeperDependencies.getDependencyConfigurations().collect { it.path }
    }

    private static Map<String, MicroserviceConfiguration.Dependency.StubsConfiguration> getStubsFromZookeeper(ZookeeperDependencies zookeeperDependencies) {
        return zookeeperDependencies.getDependencyConfigurations().collectEntries { ZookeeperDependency dependency ->
            MicroserviceConfiguration.Dependency.StubsConfiguration stubsConfiguration
            if (dependency.stubs) {
                stubsConfiguration = new MicroserviceConfiguration.Dependency.StubsConfiguration(dependency.stubsConfiguration.stubsGroupId, dependency.stubsConfiguration.stubsArtifactId, dependency.stubsConfiguration.stubsClassifier)
            } else {
                stubsConfiguration = new MicroserviceConfiguration.Dependency.StubsConfiguration(new ServicePath(dependency.path))
            }
            return [dependency.path, stubsConfiguration]
        }
    }

}
