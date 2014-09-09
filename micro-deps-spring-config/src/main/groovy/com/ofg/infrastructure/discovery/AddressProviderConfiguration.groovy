package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/**
 * Configuration that registers a bean related to microservice's address and port providing. 
 * 
 * @see MicroserviceAddressProvider
 */
@Configuration
@CompileStatic
class AddressProviderConfiguration {
    
    @PackageScope static final Integer DEFAULT_SERVER_PORT = 8080

    @Autowired Environment environment

    @Bean
    @PackageScope
    MicroserviceAddressProvider microserviceAddressProvider(
            @Value('${microservice.host:#{T(com.ofg.infrastructure.discovery.AddressProviderConfiguration).resolveMicroserviceLocalhost()}}') String microserviceHost,
            @Value('${microservice.port:#{T(com.ofg.infrastructure.discovery.AddressProviderConfiguration).resolveMicroservicePort(@environment)}}') int microservicePort) {        
        return new MicroserviceAddressProvider(microserviceHost, microservicePort)
    }
    
    static String resolveMicroserviceLocalhost() {
        return InetAddress.localHost.hostAddress
    }
    
    static Integer resolveMicroservicePort(Environment environment) {
        String port = System.getProperty('port') ?: environment.getProperty('server.port')
        return port != null ? port.toInteger() : DEFAULT_SERVER_PORT
    }
}
