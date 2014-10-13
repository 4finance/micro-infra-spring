package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.correlationid.CorrelationIdConfiguration
import com.ofg.infrastructure.web.view.ViewConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@TypeChecked
@Configuration
@Import([CorrelationIdConfiguration, ViewConfiguration])
class ConfigurationWithoutServiceDiscovery {
}
