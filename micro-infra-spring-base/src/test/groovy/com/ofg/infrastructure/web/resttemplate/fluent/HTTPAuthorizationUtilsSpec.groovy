package com.ofg.infrastructure.web.resttemplate.fluent

import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.infrastructure.web.resttemplate.fluent.HTTPAuthorizationUtils.encodeCredentials

class HTTPAuthorizationUtilsSpec extends Specification {

    def "encode basic authentication #username and #password credentials into #authorizationValue"() {
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
