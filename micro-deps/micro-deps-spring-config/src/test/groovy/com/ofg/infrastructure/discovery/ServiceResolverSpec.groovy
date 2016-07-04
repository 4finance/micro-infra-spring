package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

import static com.ofg.config.BasicProfiles.TEST
import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.*

class ServiceResolverSpec extends Specification {

    def 'should resolve urls properly'() {
        given:
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()
            applicationContext.environment.setActiveProfiles(TEST)
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
            serviceResolver.getUri(new ServicePath("/com/ofg/foo/bar")).isPresent()
        cleanup:
            applicationContext.close()
    }

    void setupStubs(ServiceConfigurationResolver serviceConfigurationResolver, CuratorFramework curatorFramework) {
        serviceConfigurationResolver.dependencies.each {
            ServiceInstance<Map> serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/${it.servicePath.path}"))
                    .address('localhost')
                    .port(8030)
                    .name(it.serviceAlias.name)
                    .build()
            ServiceDiscoveryBuilder.builder(Map).basePath(serviceConfigurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build().start()
        }
    }

}