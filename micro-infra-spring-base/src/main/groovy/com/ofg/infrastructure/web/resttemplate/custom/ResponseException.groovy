package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

@CompileStatic
class ResponseException extends RuntimeException {

    final HttpStatus httpStatus
    final HttpHeaders headers
    final String body

    ResponseException(HttpStatus httpStatus, String body, HttpHeaders headers) {
        this.httpStatus = httpStatus
        this.body = body
        this.headers = headers
    }

    String getMessage() {
        return "Status code: [$httpStatus], Headers: [$headers], Body: [$body]";
    }

}
