package com.ofg.infrastructure.discovery
import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

class ServiceResolverSpec extends Specification {    
    
    def 'should resolve urls properly'() {
        given:
            TestingServer testingServer = new TestingServer(2181)
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()
            applicationContext.register(PropertySourceConfiguration, ServiceResolverConfiguration)
            applicationContext.refresh()
        and:
            ServiceConfigurationResolver serviceConfigurationResolver = applicationContext.getBean(ServiceConfigurationResolver) 
            CuratorFramework curatorFramework = applicationContext.getBean(CuratorFramework)
            ServiceResolver serviceResolver = applicationContext.getBean(ServiceResolver)
        and:
            setupStubs(serviceConfigurationResolver, curatorFramework)
            serviceResolver.start()
        expect:
            serviceResolver.getUrl("collerator") == Optional.of('http://localhost:8030/collerator')
        cleanup:
            applicationContext.close()
            testingServer.close()
    }
    
    void setupStubs(ServiceConfigurationResolver serviceConfigurationResolver, CuratorFramework curatorFramework) {
        serviceConfigurationResolver.dependencies.each {
            ServiceInstance<Void> serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/${it.key}"))
                    .address('localhost')
                    .port(8030)
                    .name(it.value)
                    .build()
            ServiceDiscoveryBuilder.builder(Void).basePath(serviceConfigurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build().start()
        }
    }
   
}
