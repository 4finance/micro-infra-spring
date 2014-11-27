package com.ofg.infrastructure.discovery

import spock.lang.Specification

import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.*

class ServiceConfigurationResolverSpec extends Specification {

    def 'should parse valid configuration'() {
        when:
            def resolver = new ServiceConfigurationResolver(VALID_CONFIGURATION)
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

    def 'should fail on invalid collaborator element'() {
        when:
            new ServiceConfigurationResolver(INVALID_COLLABORATOR_ELEMENT)
        then:
            def e = thrown(InvalidMicroserviceConfigurationException)
            print e.message
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
}
