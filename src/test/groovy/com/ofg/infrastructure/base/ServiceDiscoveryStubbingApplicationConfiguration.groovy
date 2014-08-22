package com.ofg.infrastructure.base

import com.ofg.infrastructure.discovery.ServiceDiscoveryStubbingConfiguration
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@TypeChecked
@Configuration
@Import([ServiceDiscoveryStubbingConfiguration, WebInfrastructureConfiguration])
class ServiceDiscoveryStubbingApplicationConfiguration {


}
