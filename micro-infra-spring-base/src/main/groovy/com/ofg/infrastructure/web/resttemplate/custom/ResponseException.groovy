package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.http.HttpStatus

@CompileStatic
class ResponseException extends RuntimeException {

    HttpStatus httpStatus

    ResponseException(HttpStatus httpStatus, String message) {
        super(message)
        this.httpStatus = httpStatus
    }

    String getMessage() {
        return "Status code [$httpStatus], Body: ${super.getMessage()}";
    }

}
