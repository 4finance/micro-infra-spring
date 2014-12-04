package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.ConfigWithEnvironment
import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

class ServiceResolverConfigurationSpec extends Specification {

    def "should resolve microservice port to the one in system prop"() {
        given:
            Integer expectedPort = 12345
            String previousProp = System.setProperty('port', expectedPort.toString())            
        when:
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext(PropertySourceConfiguration, AddressProviderConfiguration)
        then:
            applicationContext.getBean(MicroserviceAddressProvider).port == expectedPort
        cleanup:
            System.setProperty('port', previousProp ?: '')
    }

    def "should resolve microservice port to the one in properties file when there is no system prop set"() {
        given:
            String previousProp = System.setProperty('port', '')
        and:
            Integer expectedPort = 1234567
        when:
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext(PropertySourceConfiguration, ConfigWithEnvironment, AddressProviderConfiguration)
        then:
            applicationContext.getBean(MicroserviceAddressProvider).port == expectedPort
        cleanup:
            System.setProperty('port', previousProp ?: '')
    }
    
    def "should resolve microservice port to default value when there is no system prop nor environment prop"() {
        given:
            String previousProp = System.setProperty('port', '')
        when:
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext(PropertySourceConfiguration, AddressProviderConfiguration)
        then:
            applicationContext.getBean(MicroserviceAddressProvider).port == 8080
        cleanup:
            System.setProperty('port', previousProp ?: '')
    }    
}
