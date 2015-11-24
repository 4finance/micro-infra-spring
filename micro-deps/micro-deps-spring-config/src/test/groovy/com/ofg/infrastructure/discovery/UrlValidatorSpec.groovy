package com.ofg.infrastructure.discovery

import spock.lang.Specification
import spock.lang.Unroll

class UrlValidatorSpec extends Specification {

    @Unroll
    def "should result in [#result] for string [#string]"() {
        expect:
            result == (UrlValidator.isValidUrl(string) != null)
        where:
            string                || result
            'localhost'           || true
            '123.123.123.123'     || true
            '123.home'            || false
            'correlator'          || false
            'backoffice-vivus-pl' || false
            'backoffice'          || false

    }

}
