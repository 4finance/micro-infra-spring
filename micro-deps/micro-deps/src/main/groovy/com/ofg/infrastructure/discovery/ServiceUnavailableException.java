package com.ofg.infrastructure.discovery;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String serviceName) {
        super("Service with name [" + serviceName + "] is unavailable");
    }
}
