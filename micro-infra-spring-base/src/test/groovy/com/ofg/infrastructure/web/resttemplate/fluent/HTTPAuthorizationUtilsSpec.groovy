package com.ofg.infrastructure.web.resttemplate.fluent

import spock.lang.Specification

import static com.ofg.infrastructure.web.resttemplate.fluent.HTTPAuthorizationUtils.encodeCredentials

class HTTPAuthorizationUtilsSpec extends Specification {

    def "encode basic authentication credentials"() {
        when:
            def encodedResult = encodeCredentials(username, password)
        then:
            encodedResult == authorizationValue
        where:
            username  | password      || authorizationValue
            'Aladdin' | 'open sesame' || 'QWxhZGRpbjpvcGVuIHNlc2FtZQ=='
            'Denis'   | "Denis123"    || 'RGVuaXM6RGVuaXMxMjM='
    }

}
