package com.ofg.stub.mapping

import groovy.transform.CompileStatic

@CompileStatic
class InvalidRepositoryLayout extends RuntimeException {
    InvalidRepositoryLayout(String message) {
        super(message)
    }
}
