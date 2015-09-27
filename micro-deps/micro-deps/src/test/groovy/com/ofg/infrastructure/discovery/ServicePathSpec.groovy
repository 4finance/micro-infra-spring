package com.ofg.infrastructure.discovery

import spock.lang.Specification
import spock.lang.Unroll

class ServicePathSpec extends Specification {

    @Unroll
    def "should retrieve last name [#expectedLastName] for path [#path]"() {
        expect:
            new ServicePath(path).lastName == expectedLastName
        where:
            path                         || expectedLastName
            '/io/fourfinanceit/somename' || 'somename'
            'somename'                   || 'somename'
            null                         || ''
    }

    @Unroll
    def "should retrieve path to last name [#expectedPathToLastName] for path [#path]"() {
        expect:
            new ServicePath(path).pathToLastName == expectedPathToLastName
        where:
            path                         || expectedPathToLastName
            '/io/fourfinanceit/somename' || '/io/fourfinanceit'
            'somename'                   || 'somename'
            null                         || ''
    }
}
