package com.ofg.infrastructure.web.exception

import groovy.transform.CompileStatic
import org.springframework.validation.ObjectError

@CompileStatic
class BadParametersException extends RuntimeException {
    List<ObjectError> errors

    public BadParametersException(List<ObjectError> errors) {
        this.errors = errors
    }

    public List<ObjectError> getErrors() {
        return errors
    }
}
