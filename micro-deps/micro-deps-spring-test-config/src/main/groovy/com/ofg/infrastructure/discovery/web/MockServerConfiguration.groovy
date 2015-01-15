package com.ofg.infrastructure.discovery.web

import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

/**
 * Configuration that registers {@link HttpMockServer} as a Spring bean. Takes care
 * of graceful shutdown process.
 *
 * @see HttpMockServer
 */
@CompileStatic
@Configuration
class MockServerConfiguration {

    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(initMethod = 'start', destroyMethod = 'shutdownServer')
    HttpMockServer httpMockServer(AvailablePortScanner availablePortScanner) {
        return new HttpMockServer(availablePortScanner.nextAvailablePort())
    }

    @Bean
    AvailablePortScanner availablePortScanner() {
        return new AvailablePortScanner(8030, 10000)
    }

}
