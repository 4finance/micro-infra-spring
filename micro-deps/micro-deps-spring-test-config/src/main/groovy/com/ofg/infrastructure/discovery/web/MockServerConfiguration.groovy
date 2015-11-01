package com.ofg.infrastructure.discovery.web

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.stub.StubConfiguration
import com.ofg.infrastructure.stub.Stubs
import com.ofg.stub.StubRunning
import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration that registers {@link HttpMockServer} as a Spring bean. Takes care
 * of graceful shutdown process.
 *
 * @see HttpMockServer
 */
@CompileStatic
@Configuration
@Import(StubConfiguration)
class MockServerConfiguration {

    @Autowired(required = false) ServiceConfigurationResolver configurationResolver
    @Autowired(required = false) ZookeeperDependencies zookeeperDependencies

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
    Stubs stubs(StubRunning stubRunning) {
        if (zookeeperDependencies) {
            return new Stubs(zookeeperDependencies, stubRunning)
        }
        return new Stubs(configurationResolver, stubRunning)
    }

}
