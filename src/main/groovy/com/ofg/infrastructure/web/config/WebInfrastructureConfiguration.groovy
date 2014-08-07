package com.ofg.infrastructure.web.config

import com.ofg.infrastructure.web.exception.ControllerExceptionConfiguration
import com.ofg.infrastructure.web.filter.FilterConfiguration
import com.ofg.infrastructure.web.resttemplate.RestTemplateConfiguration
import com.ofg.infrastructure.web.view.ViewConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@TypeChecked
@Import([RestTemplateConfiguration,
        ControllerExceptionConfiguration,
        FilterConfiguration,
        ViewConfiguration])
class WebInfrastructureConfiguration {

}
