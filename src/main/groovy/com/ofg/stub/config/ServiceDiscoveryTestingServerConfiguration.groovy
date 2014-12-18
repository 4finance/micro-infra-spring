package com.ofg.stub.config

import com.google.common.base.Strings
import com.google.common.net.HostAndPort
import groovy.transform.CompileStatic
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.SocketUtils

@Configuration
@CompileStatic
class ServiceDiscoveryTestingServerConfiguration {

    /**
     * Provides test instance of Zookeeper. If {@code serviceResolverUrl} is {@code null} or an empty string a randomly selected, available port is used.
     *
     * @param serviceResolverUrl - host with port where your application will search for Zookeeper instance
     * 
     * @throws IllegalArgumentException when port in provided URL is not parseable, i.e. it is not numeric value
     */
    @Bean(destroyMethod = 'close')
    TestingServer testingServer(@Value('${service.resolver.url:}') String serviceResolverUrl) {
        String serviceUrl = Strings.nullToEmpty(serviceResolverUrl)
        int defaultPort = SocketUtils.findAvailableTcpPort()
        return new TestingServer(HostAndPort.fromString(serviceUrl).getPortOrDefault(defaultPort))
    }
}
