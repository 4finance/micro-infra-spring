package com.ofg.infrastructure.web.exception

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@TypeChecked
@Configuration
class ControllerExceptionConfiguration {

    @Bean
    ControllerExceptionHandler controllerExceptionHandler() {
        return new ControllerExceptionHandler()
    }

}
