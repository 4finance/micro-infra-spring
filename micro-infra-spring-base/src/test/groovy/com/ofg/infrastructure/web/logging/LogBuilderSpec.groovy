package com.ofg.infrastructure.web.logging

import spock.lang.Specification

class LogBuilderSpec extends Specification {

    private final String URL = '/any/url/string'
    private final String METHOD = 'any method'
    private final int STATUS = 200
    private final String ANY_JSON = '{any : "any-data"}'

    /*
    def 'Should build request log string for correct provided data'() {
        when:
            String logString = LogBuilder.createLogBuilder()
                                            .withHttpMethod(METHOD)
                                            .withURI(URL)
                                            .withHttpStatus(STATUS)
                                            .withContent(ANY_JSON)
                                            .build()
        then:
                logString.contains(METHOD)
            and:
                logString.contains(URL)
            and:
                logString.contains(STATUS.toString())
            and:
                logString.contains(ANY_JSON)
    }*/
}
