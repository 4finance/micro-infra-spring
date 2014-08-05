package com.ofg.infrastructure.discovery

import groovy.transform.TypeChecked

@TypeChecked
class MicroserviceAddressProvider {

    private final String host
    private final int port

    MicroserviceAddressProvider(String microserviceHost, int microservicePort) {
        this.host = microserviceHost
        this.port = microservicePort
    }

    String getHost() {        
        return host
    }

    int getPort() {
        return port
    }
}
