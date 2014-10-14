package com.ofg.infrastructure.web.exception

import groovy.transform.CompileStatic

@CompileStatic
class BadParameterError {
    String field
    String message

    BadParameterError(String field, String message) {
        this.field = field
        this.message = message
    }
}
