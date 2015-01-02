package com.ofg.stub.util

import spock.lang.Specification

class PortResolverSpec extends Specification {

    def "returns IllegarArgumentException when bad format"() {
        when:
        PortResolver.getPortFromUrlOrRandom("qwerty")
        then:
        thrown(IllegalArgumentException)
    }

    def "returns NullPointerException when null"() {
        when:
        PortResolver.getPortFromUrlOrRandom(null)
        then:
        thrown(NullPointerException)
    }

    def "returns correct port"() {
        when:
        int random = PortResolver.getPortFromUrlOrRandom("localhost:2000")
        then:
        random == 2000
    }

    def "return -1 if no port present"() {
        when:
        int port = PortResolver.getPortFromUrlOrRandom("localhost:")
        then:
        port == -1
    }

}
