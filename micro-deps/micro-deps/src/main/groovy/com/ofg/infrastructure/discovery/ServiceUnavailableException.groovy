package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic

@CompileStatic
class ServiceUnavailableException extends RuntimeException {
    ServiceUnavailableException(String serviceName) {
        super("Service with name [$serviceName] is unavailable")
    }
}
