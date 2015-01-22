package com.ofg.infrastructure.discovery.web
import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.CompileStatic
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
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

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

}
