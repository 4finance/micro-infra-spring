package com.ofg.stub.mapping

import spock.lang.Specification

class DescriptorConstantsSpec extends Specification {

    def 'should throw an exception when trying to instantiate the class'() {
        when:
            DescriptorConstants.newInstance()
        then:
            thrown(IllegalAccessException)
    }
}
