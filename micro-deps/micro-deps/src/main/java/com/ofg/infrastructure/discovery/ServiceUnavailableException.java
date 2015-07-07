package com.ofg.infrastructure.discovery;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(ServicePath path) {
        super("No services availabe under path [" + path + "]");
    }

    /**
     *
     * @deprecated since 0.9.1, use {@link #ServiceUnavailableException(ServicePath path)} instead
     */
    @Deprecated
    public ServiceUnavailableException(String path) {
        super("No services availabe under path [" + path + "]");
    }
}
