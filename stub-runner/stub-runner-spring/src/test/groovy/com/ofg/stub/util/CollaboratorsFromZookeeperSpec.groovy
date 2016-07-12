package com.ofg.stub.util

import com.ofg.stub.Collaborators
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties
import org.springframework.cloud.zookeeper.discovery.dependency.StubsConfiguration
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependency
import spock.lang.Specification

class CollaboratorsFromZookeeperSpec extends Specification {

    def "should create collaborator with default stubs"() {
        given:
            ZookeeperDependencies dependencies = new ZookeeperDependencies()
            String dependencyPath = 'pl/any/service'
            ZookeeperDiscoveryProperties discoveryProperties = Stub(ZookeeperDiscoveryProperties)
            discoveryProperties.root >> dependencyPath
            ZookeeperDependency dependency = new ZookeeperDependency(dependencyPath)
            dependencies.setDependencies(['service': dependency])

        when:
            Collaborators collaborators = CollaboratorsFromZookeeper.fromZookeeperDependencies(discoveryProperties, dependencies)
        then:
            collaborators.basePath == dependencyPath
            collaborators.stubsPaths.get(dependencyPath).toColonSeparatedDependencyNotation() == 'pl.any:service:stubs'
    }

    def "should create collaborator with defined stubs"() {
        given:
            ZookeeperDependencies dependencies = new ZookeeperDependencies()
            String dependencyPath = 'pl/any/service'
            ZookeeperDiscoveryProperties discoveryProperties = Stub(ZookeeperDiscoveryProperties)
            discoveryProperties.root >> dependencyPath
            ZookeeperDependency dependency = new ZookeeperDependency(dependencyPath)
            String stubPath = 'com.different:service:stub'
            dependency.stubs = stubPath
            dependency.stubsConfiguration = new StubsConfiguration(stubPath)
            dependencies.setDependencies(['service': dependency])

        when:
            Collaborators collaborators = CollaboratorsFromZookeeper.fromZookeeperDependencies(discoveryProperties, dependencies)
        then:
            collaborators.basePath == dependencyPath
            collaborators.stubsPaths.get(dependencyPath).toColonSeparatedDependencyNotation() == stubPath
    }
}
