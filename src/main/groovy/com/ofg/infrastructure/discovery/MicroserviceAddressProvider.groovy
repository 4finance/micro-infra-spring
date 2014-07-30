package com.ofg.infrastructure.discovery
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

@TypeChecked
@PackageScope
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
