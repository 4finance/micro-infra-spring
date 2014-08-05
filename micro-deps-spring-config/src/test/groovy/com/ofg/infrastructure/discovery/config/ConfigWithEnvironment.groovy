package com.ofg.infrastructure.discovery.config

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@TypeChecked
@Configuration
@PropertySource('classpath:microservice.properties')
class ConfigWithEnvironment {

}
