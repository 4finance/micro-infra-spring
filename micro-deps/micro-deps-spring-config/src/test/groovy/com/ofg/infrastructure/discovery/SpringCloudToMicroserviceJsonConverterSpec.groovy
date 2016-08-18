package com.ofg.infrastructure.discovery

import groovy.json.JsonSlurper
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties
import org.springframework.cloud.zookeeper.discovery.dependency.StubsConfiguration
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependency
import spock.lang.Specification

class SpringCloudToMicroserviceJsonConverterSpec extends Specification {

    def "should create collaborator with default stubs"() {
        given:
        ZookeeperDependencies dependencies = new ZookeeperDependencies()
        String dependencyPath = 'pl/any/service'
        ZookeeperDiscoveryProperties discoveryProperties = Stub(ZookeeperDiscoveryProperties)
        discoveryProperties.root >> dependencyPath
        ZookeeperDependency dependency = new ZookeeperDependency(dependencyPath)
        dependencies.setDependencies(['service': dependency])
        SpringCloudToMicroserviceJsonConverter converter = new SpringCloudToMicroserviceJsonConverter("base", dependencies, discoveryProperties)

        when:
        println converter.toMicroserviceJsonNotation()
        def dependenciesMicroserviceJson = new JsonSlurper().parseText(converter.toMicroserviceJsonNotation())

        then:
        dependenciesMicroserviceJson == [
                "planyservice": [
                        "this"        : "base",
                        "dependencies": [
                                "service": [
                                        "path"               : "pl/any/service",
                                        "stubs"              : "pl.any:service:stubs",
                                        "load-balancer"      : "ROUND_ROBIN",
                                        "required"           : false,
                                        "contentTypeTemplate": "",
                                        "headers"            : [:],
                                        "version"            : ""
                                ]
                        ]
                ]
        ]
    }

    def "should convert dependency with defined stubs"() {
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
        SpringCloudToMicroserviceJsonConverter converter = new SpringCloudToMicroserviceJsonConverter("base", dependencies, discoveryProperties)

        when:
        def dependenciesMicroserviceJson = new JsonSlurper().parseText(converter.toMicroserviceJsonNotation())

        then:
        dependenciesMicroserviceJson == [
                "planyservice": [
                        "this"        : "base",
                        "dependencies": [
                                "service": [
                                        "path"               : "pl/any/service",
                                        "stubs"              : "com.different:service:stub",
                                        "load-balancer"      : "ROUND_ROBIN",
                                        "required"           : false,
                                        "contentTypeTemplate": "",
                                        "headers"            : [:],
                                        "version"            : ""
                                ]
                        ]
                ]
        ]
    }

}
