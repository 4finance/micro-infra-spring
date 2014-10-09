package com.ofg.infrastructure.discovery.config

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource('classpath:microservice.properties')
@CompileStatic
class ConfigWithEnvironment {
}
