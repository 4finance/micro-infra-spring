package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import groovyjarjarantlr.collections.List
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.io.Resource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.ofg.config.BasicProfiles.TEST

@ContextConfiguration(classes = Config)
@ActiveProfiles(TEST)
class ServiceInstancePayloadSpec extends Specification {

    @Autowired ServiceDiscovery serviceDiscovery

    def 'should payload of every service instance be instance details'() {
        given:
            List<ServiceInstance> instances = serviceDiscovery.queryForInstances('com/ofg/stub-runner/tester') as List
        expect:
            instances.each { assert it.payload.class == InstanceDetails }
    }

    @Configuration
    @Import([PropertySourceConfiguration, ServiceResolverConfiguration])
    static class Config {

        @Bean
        ServiceConfigurationResolver serviceConfigurationResolver(
                @Value('${microservice.config.file}') Resource microserviceConfig) {
            return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
        }

    }

}