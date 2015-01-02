package com.ofg.stub.util

import spock.lang.Specification
import com.google.common.base.Optional

class PortResolverSpec extends Specification {

    def "returns NullPointerException when null"() {
        when:
        PortResolver.tryGetPortFromUrl(null)
        then:
        thrown(NullPointerException)
    }

    def "returns correct port"() {
        when:
        Optional<Integer> random = PortResolver.tryGetPortFromUrl("localhost:2000")
        then:
        random.get() == 2000
    }

    def "return -1 if no port present"() {
        when:
        Optional<Integer> port = PortResolver.tryGetPortFromUrl("localhost:")
        then:
        !port.isPresent()
    }

}
