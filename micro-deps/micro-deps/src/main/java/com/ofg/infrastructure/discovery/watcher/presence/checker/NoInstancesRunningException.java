package com.ofg.infrastructure.discovery.watcher.presence.checker;

public class NoInstancesRunningException extends RuntimeException {
    public NoInstancesRunningException(String dependencyName) {
        super("Required microservice dependency with name [" + dependencyName + "] is missing");
    }
}
