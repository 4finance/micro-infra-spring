package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import org.apache.curator.test.TestingServer
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.config.BasicProfiles.*

class DependencyResolutionConfigurationSpec extends Specification {

    private static final int DEFAULT_ZOOKEEPER_PORT = 2181

    private static final Class<StubbedServiceResolver> STUB_RESOLVER = StubbedServiceResolver
    private static final Class<ZookeeperServiceResolver> REAL_RESOLVER = ZookeeperServiceResolver

    @Shared private TestingServer testingServer

    def setupSpec() {
        testingServer = new TestingServer(DEFAULT_ZOOKEEPER_PORT)
    }

    @Unroll
    def 'should use [#expectedResolver.name] implementation of ServiceResolver when [#profile] profile is active'() {
        given:
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()
            applicationContext.register(PropertySourceConfiguration, ServiceResolverConfiguration)
        and:
            applicationContext.environment.setActiveProfiles(profile)
            applicationContext.refresh()
        when:
            ServiceResolver resolver = applicationContext.getBean(ServiceResolver)
        then:
            resolver.class == expectedResolver
        cleanup:
            applicationContext.close()
        where:
            profile     || expectedResolver
            TEST        || STUB_RESOLVER
            DEVELOPMENT || STUB_RESOLVER
            PRODUCTION  || REAL_RESOLVER
    }

    def cleanupSpec() {
        testingServer.close()
    }
}
