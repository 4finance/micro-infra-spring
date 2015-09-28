package com.ofg.infrastructure.discovery

import spock.lang.Specification

import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration

class StubsConfigurationSpec extends Specification {

    def "should print stubs configuration in Gradle notation"() {
        expect:
            new StubsConfiguration('foo.bar', 'artifact', 'classifier').toGradleNotation() == 'foo.bar:artifact:classifier'
    }
}
