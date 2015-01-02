package com.ofg.stub.util

import spock.lang.Specification
import com.google.common.base.Optional

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
        Optional<Integer> random = PortResolver.getPortFromUrlOrRandom("localhost:2000")
        then:
        random.get() == 2000
    }

    def "return -1 if no port present"() {
        when:
        Optional<Integer> port = PortResolver.getPortFromUrlOrRandom("localhost:")
        then:
        !port.isPresent()
    }

}
