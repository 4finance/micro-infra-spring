package com.ofg.infrastructure.discovery.config

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@CompileStatic
class PropertySourceConfiguration {

    @Bean static PropertySourcesPlaceholderConfigurer propertiesConfigurer() {
        Properties properties = new Properties()
        properties.setProperty('stubrunner.stubs.repository.root', 'http://dl.bintray.com/4finance/micro')
        properties.setProperty('stubrunner.stubs.group', 'com.ofg')
        properties.setProperty('stubrunner.stubs.module', 'stub-runner-examples')
        properties.setProperty('microservice.config.file', 'classpath:stub-microservice.json')
        return new PropertySourcesPlaceholderConfigurer(properties: properties)
    }

}
