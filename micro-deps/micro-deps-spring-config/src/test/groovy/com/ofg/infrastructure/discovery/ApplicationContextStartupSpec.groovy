package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import com.ofg.infrastructure.discovery.watcher.presence.checker.NoInstancesRunningException
import com.ofg.stub.server.AvailablePortScanner
import org.apache.curator.test.TestingServer
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static com.ofg.config.BasicProfiles.PRODUCTION
import static org.codehaus.groovy.runtime.StackTraceUtils.extractRootCause

class ApplicationContextStartupSpec extends Specification {

    @RestoreSystemProperties
    def 'should fail to start application context if resource is missing when default bean is missing deps'() {
        given:
            TestingServer testingServer
            Integer port = new AvailablePortScanner(9000, 9500).tryToExecuteWithFreePort { int freePort ->
                testingServer = new TestingServer(freePort)
                return freePort
            }
            System.setProperty('service.resolver.url', "localhost:$port")
        and:
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()
            applicationContext.environment.setActiveProfiles(PRODUCTION)
            applicationContext.register(PropertySourceConfiguration, ServiceResolverConfiguration)
        when:            
            applicationContext.refresh()
        then:    
            Throwable thrown = thrown(Throwable)
            extractRootCause(thrown).class == NoInstancesRunningException
        cleanup:
            applicationContext?.close()
            testingServer?.close()
    }
         
}

