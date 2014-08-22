package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

@TypeChecked
final class HttpEntityUtils {
    private HttpEntityUtils() {
        throw new UnsupportedOperationException("Can't instantiate a utility class")
    }

    public static HttpEntity<Object> getHttpEntityFrom(Map params) {        
        HttpHeaders headers = params.headers as HttpHeaders
        HttpEntity<?> httpEntity = new HttpEntity<Object>(params.request, headers)
        return httpEntity
    }
}
