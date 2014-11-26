package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import spock.lang.Specification

class HttpEntityUtilsSpec extends Specification {

    public static final int UNKOWN_HEADER_VALUE = -1

    def 'should create HttpEntity from a filled argument map'() {
        given:
            long expectedExpires = 1000
            String expectedBody = '''{"sample":"json"}'''
            Map args = [headers: new HttpHeaders(expires: expectedExpires), request: expectedBody]
        when:
            HttpEntity httpEntity = RestExecutor.getHttpEntityFrom(args)
        then:
            expectedExpires == httpEntity.headers.getExpires()
            expectedBody == httpEntity.body
    }
    
    def 'should create HttpEntity from an empty argument map'() {
        given:
            Map args = [:]
        when:
            HttpEntity httpEntity = RestExecutor.getHttpEntityFrom(args)
        then:
            httpEntity.headers.getExpires() == UNKOWN_HEADER_VALUE
            !httpEntity.body
    }
    
}
