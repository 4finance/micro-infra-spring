package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import groovy.transform.TypeChecked

@TypeChecked
class InvalidHttpMethodParametersException extends RuntimeException {
    InvalidHttpMethodParametersException(Map params) {
        super("Invalid args [$params] passed to method")
    }
}
