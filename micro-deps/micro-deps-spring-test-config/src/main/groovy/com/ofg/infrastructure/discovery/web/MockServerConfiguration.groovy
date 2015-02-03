package com.ofg.infrastructure.discovery.web

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.stub.Stubs
import com.ofg.stub.StubRunning
import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
/**
 * Configuration that registers {@link HttpMockServer} as a Spring bean. Takes care
 * of graceful shutdown process.
 *
 * @see HttpMockServer
 */
@CompileStatic
@Configuration
class MockServerConfiguration {

    @Bean(destroyMethod = 'shutdownServer')
    HttpMockServer httpMockServer(AvailablePortScanner availablePortScanner) {
        HttpMockServer httpMockServer = null
        return availablePortScanner.tryToExecuteWithFreePort { int availablePort ->
            httpMockServer = new HttpMockServer(availablePort)
            httpMockServer.start()
            return httpMockServer
        }
    }

    @Bean
    AvailablePortScanner availablePortScanner() {
        return new AvailablePortScanner(8030, 10000)
    }

    @Bean(destroyMethod = 'shutdown')
    Stubs stubs(ServiceConfigurationResolver configurationResolver, StubRunning stubRunning) {
        return new Stubs(configurationResolver, stubRunning)
    }

}
