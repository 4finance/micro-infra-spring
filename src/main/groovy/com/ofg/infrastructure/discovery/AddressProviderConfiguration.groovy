package com.ofg.infrastructure.discovery
import groovy.transform.PackageScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class AddressProviderConfiguration {
    
    @PackageScope static final Integer DEFAULT_SERVER_PORT = 8080

    @Autowired Environment environment

    @Bean
    @PackageScope
    MicroserviceAddressProvider microserviceAddressProvider(
            @Value('${microservice.host:#{@addressProviderConfiguration.resolveMicroserviceLocalhost()}}') String microserviceHost,
            @Value('${microservice.port:#{@addressProviderConfiguration.resolveMicroservicePort()}}') int microservicePort) {        
        return new MicroserviceAddressProvider(microserviceHost, microservicePort)
    }
    
    String resolveMicroserviceLocalhost() {
        return InetAddress.localHost.hostAddress
    }
    
    Integer resolveMicroservicePort() {
        String port = System.getProperty('port') ?: environment.getProperty('server.port')
        return port != null ? port.toInteger() : DEFAULT_SERVER_PORT
    }
}
