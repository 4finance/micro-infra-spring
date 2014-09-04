package com.ofg.infrastructure.web.exception
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration that initializes the {@link ControllerExceptionHandler} as a bean
 */
@CompileStatic
@Configuration
class ControllerExceptionConfiguration {

    @Bean
    ControllerExceptionHandler controllerExceptionHandler() {
        return new ControllerExceptionHandler()
    }

}
