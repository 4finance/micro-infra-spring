package com.ofg.infrastructure.discovery.config

import com.ofg.infrastructure.discovery.ApplicationContextStartupSpec
import groovy.transform.TypeChecked
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*

@TypeChecked
@Configuration
@PropertySource('classpath:application-stub.properties')
@Profile(ApplicationContextStartupSpec.CONTEXT_FOR_ZOOKEEPER_WITHOUT_STUBS_PROFILE)
@EnableAspectJAutoProxy(proxyTargetClass = true)
class ApplicationContextWithoutStubsConfiguration {

    @Bean(destroyMethod = 'close')
    TestingServer testingServer(@Value('${service.resolver.url:localhost:2182}') String serviceResolverUrl) {
        return new TestingServer(serviceResolverUrl.split(':').last().toInteger())
    }    
    
}
