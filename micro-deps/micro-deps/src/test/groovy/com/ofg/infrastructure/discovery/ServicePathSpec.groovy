package com.ofg.infrastructure.discovery

import spock.lang.Specification

class ServicePathSpec extends Specification {
    def "should retrieve last part of app name"() {
        given:
            String path = '/io/fourfinanceit/somename'
        expect:
            new ServicePath(path).lastName == 'somename'
    }

    def "should retrieve path with a starting slash"() {
        given:
            String path = 'io/fourfinanceit/somename'
        expect:
            new ServicePath(path).pathWithStartingSlash == "/$path"
    }
}
