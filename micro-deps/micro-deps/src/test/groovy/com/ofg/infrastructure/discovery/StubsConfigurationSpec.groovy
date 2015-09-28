package com.ofg.infrastructure.discovery

import spock.lang.Specification

import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration

class StubsConfigurationSpec extends Specification {

    def "should print stubs configuration in colon separated notation"() {
        expect:
            new StubsConfiguration('foo.bar', 'artifact', 'classifier').toColonSeparatedDependencyNotation() == 'foo.bar:artifact:classifier'
    }
}
