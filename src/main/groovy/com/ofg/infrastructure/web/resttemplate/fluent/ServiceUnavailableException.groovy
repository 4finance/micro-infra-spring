package com.ofg.infrastructure.web.resttemplate.fluent

import groovy.transform.TypeChecked

@TypeChecked
class ServiceUnavailableException extends RuntimeException {

    ServiceUnavailableException(String serviceName) {
        super("Service with name [$serviceName] is unavailable")
    }
    
}
