package com.ofg.infrastructure.web.resttemplate.custom

import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

class HttpStatusVerifierSpec extends Specification {

    def 'should verify that HTTP status [#status] is error [#error]'() {
        expect:
            error == HttpStatusVerifier.isError(status)
        where:
            status                           || error
            HttpStatus.PROCESSING            || false
            HttpStatus.OK                    || false
            HttpStatus.TEMPORARY_REDIRECT    || false
            HttpStatus.INTERNAL_SERVER_ERROR || true
            HttpStatus.BAD_REQUEST           || true

    }
}
