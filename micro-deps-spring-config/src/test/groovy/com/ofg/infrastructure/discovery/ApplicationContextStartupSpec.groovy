package com.ofg.infrastructure.discovery

import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.discovery.config.FailOnMissingDepsConfiguration
import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import com.ofg.infrastructure.discovery.watcher.presence.checker.NoInstancesRunningException
import org.apache.curator.test.TestingServer
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

import static org.codehaus.groovy.runtime.StackTraceUtils.extractRootCause

class ApplicationContextStartupSpec extends Specification {

    public static final String CONTEXT_FOR_ZOOKEEPER_WITHOUT_STUBS_PROFILE = BasicProfiles.PRODUCTION

    def 'should fail to start application context if resource is missing when default bean is missing deps'() {
        given:
            TestingServer testingServer = new TestingServer(2181)
        and:
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()
            applicationContext.environment.setActiveProfiles(CONTEXT_FOR_ZOOKEEPER_WITHOUT_STUBS_PROFILE)
            applicationContext.register(PropertySourceConfiguration, FailOnMissingDepsConfiguration, ServiceResolverConfiguration)
        when:            
            applicationContext.refresh()
        then:    
            Throwable thrown = thrown(Throwable)
            extractRootCause(thrown).class == NoInstancesRunningException
        cleanup:
            applicationContext.close()
            testingServer.close()
    }
         
}

