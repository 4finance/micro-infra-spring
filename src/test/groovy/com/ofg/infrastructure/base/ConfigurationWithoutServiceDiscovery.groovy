package com.ofg.infrastructure.base

import com.ofg.infrastructure.web.exception.ControllerExceptionConfiguration
import com.ofg.infrastructure.web.filter.FilterConfiguration
import com.ofg.infrastructure.web.view.ViewConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@TypeChecked
@Configuration
@Import([ControllerExceptionConfiguration,
        FilterConfiguration,
        ViewConfiguration])
class ConfigurationWithoutServiceDiscovery {
}
