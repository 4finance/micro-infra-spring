package com.ofg.infrastructure.discovery;

public class InvalidMicroserviceConfigurationException extends RuntimeException {
    public InvalidMicroserviceConfigurationException(String message) {
        super(message);
    }
}
