package com.ofg.infrastructure.discovery

import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.infrastructure.discovery.util.LoadBalancerType.*
import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.*

class ServiceConfigurationResolverSpec extends Specification {

    def 'should parse configuration with path element only'() {
        when:
            def resolver = new ServiceConfigurationResolver(CONFIGURATION_WITH_PATH_ELEM)
        then:
            resolver.basePath == 'pl'
            resolver.microserviceName == 'com/ofg/service'
            resolver.dependencies == ['ping':['path':'com/ofg/ping'],
                                      'pong':['path':'com/ofg/pong']]
    }

    def 'should parse flat configuration'() {
        when:
            def resolver = new ServiceConfigurationResolver(FLAT_CONFIGURATION)
        then:
            resolver.basePath == 'pl'
            resolver.microserviceName == 'com/ofg/service'
            resolver.dependencies == ['ping':['path':'com/ofg/ping'],
                                      'pong':['path':'com/ofg/pong']]
    }

    def 'should fail on missing "this" element'() {
        when:
            new ServiceConfigurationResolver(MISSING_THIS_ELEMENT)
        then:
            thrown(InvalidMicroserviceConfigurationException)
    }

    def 'should fail on invalid dependencies'() {
        when:
            new ServiceConfigurationResolver(INVALID_DEPENDENCIES_ELEMENT)
        then:
            thrown(InvalidMicroserviceConfigurationException)
    }

    def 'should fail on multiple root elements'() {
        when:
            new ServiceConfigurationResolver(MULTIPLE_ROOT_ELEMENTS)
        then:
            thrown(InvalidMicroserviceConfigurationException)
    }

    def 'should set default values for missing optional elements'() {
        when:
            def resolver = new ServiceConfigurationResolver(ONLY_REQUIRED_ELEMENTS)
        then:
            resolver.dependencies == [:]
    }

    @Unroll
    def 'should provide #loadBalancerType for given service #path'() {
        given:
            def resolver = new ServiceConfigurationResolver(LOAD_BALANCING_DEPENDENCIES)
        expect:
            resolver.getLoadBalancerTypeOf(new ServicePath(path)) == loadBalancerType
        where:
            path                 | loadBalancerType
            'com/ofg/ping'       | STICKY
            'com/ofg/pong'       | ROUND_ROBIN
            'com/ofg/some'       | RANDOM
            'com/ofg/another'    | ROUND_ROBIN
            'com/ofg/another2'   | ROUND_ROBIN
    }

    def 'should provide default round robin load balancer type for unknown service path'() {
        given:
            def resolver = new ServiceConfigurationResolver(ONLY_REQUIRED_ELEMENTS)
        expect:
            resolver.getLoadBalancerTypeOf(new ServicePath('com/ofg/other')) == ROUND_ROBIN
    }
}
