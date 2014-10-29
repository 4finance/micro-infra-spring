package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.http.HttpStatus

@CompileStatic
class ResponseException extends RuntimeException {

    HttpStatus httpStatus
    String body

    ResponseException(HttpStatus httpStatus, String body) {
        this.httpStatus = httpStatus
        this.body = body
    }

    String getMessage() {
        return "Status code [$httpStatus], Body: $body";
    }

}
