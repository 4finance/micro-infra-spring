package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import org.springframework.http.HttpHeaders
import spock.lang.Specification

class PredefinedHttpHeadersSpec extends Specification {

    def 'should set Content-Type header if template is defined'() {
        given:
            Map serviceConfig = ['version': 'v1', 'contentTypeTemplate': 'application/vnd.some-service.$version+json']
            PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders(serviceConfig)
            HttpHeaders httpHeaders = new HttpHeaders()
        when:
            predefinedHeaders.copyTo(httpHeaders)
        then:
            httpHeaders.getContentType().toString() == 'application/vnd.some-service.v1+json'
    }

    def 'should not set Content-Type header if there is no template defined'() {
        given:
            PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders([:])
            HttpHeaders httpHeaders = new HttpHeaders()
        when:
            predefinedHeaders.copyTo(httpHeaders)
        then:
            httpHeaders.getContentType() == null
            httpHeaders.get('Content-Type') == null
    }

    def 'should copy existing predefined headers to HTTP headers'() {
        given:
            Map serviceConfig = ['headers': ['header1':'value1', 'header2':'value2']]
            PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders(serviceConfig)
            HttpHeaders httpHeaders = new HttpHeaders()
        when:
            predefinedHeaders.copyTo(httpHeaders)
        then:
            httpHeaders.get('header1') == ['value1'] as List
            httpHeaders.get('header2') == ['value2'] as List
    }

}
