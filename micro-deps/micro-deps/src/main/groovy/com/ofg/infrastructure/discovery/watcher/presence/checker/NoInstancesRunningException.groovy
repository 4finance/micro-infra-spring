package com.ofg.infrastructure.discovery.watcher.presence.checker

import groovy.transform.CompileStatic

@CompileStatic
class NoInstancesRunningException extends RuntimeException {
    NoInstancesRunningException(String dependencyName) {
        super("Required microservice dependency with name [$dependencyName] is missing")
    }
}
