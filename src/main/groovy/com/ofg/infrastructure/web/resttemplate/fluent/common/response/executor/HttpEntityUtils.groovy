package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor
import groovy.transform.CompileStatic
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

@CompileStatic
final class HttpEntityUtils {
    private HttpEntityUtils() {
        throw new UnsupportedOperationException("Can't instantiate a utility class")
    }

    public static HttpEntity<Object> getHttpEntityFrom(Map params) {
        if (params.httpEntity) {
            return params.httpEntity as HttpEntity
        }
        HttpHeaders headers = params.headers as HttpHeaders
        HttpEntity<?> httpEntity = new HttpEntity<Object>(params.request, headers)
        return httpEntity
    }
}
