package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic

@CompileStatic
class ServiceNotFoundException extends RuntimeException {
    ServiceNotFoundException(String message) {
        super(message)
    }
}
