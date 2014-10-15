package com.ofg.stub.config

import groovy.transform.CompileStatic
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@CompileStatic
class ServiceDiscoveryTestingServerConfiguration {

    /**
     * Test instance of Zookeeper
     *
     * @param serviceResolverUrl - host with port where your application where search for Zookeeper instance
     */
    @Bean(destroyMethod = 'close')
    TestingServer testingServer(@Value('${service.resolver.url:localhost:2181}') String serviceResolverUrl) {
        return new TestingServer(new URI(prependProtocol(serviceResolverUrl)).port)
    }

    private static String prependProtocol(String serviceResolverUrl) {
        switch (serviceResolverUrl) {
            case ~/^http:.*$/:
                return prependProtocol('http', serviceResolverUrl)
            case ~/^https:.*$/:
                return prependProtocol('https', serviceResolverUrl)
            default:
                return serviceResolverUrl
        }
    }

    private static String prependProtocol(String protocol, String serviceResolverUrl) {
        return "$protocol://$serviceResolverUrl"
    }
}
