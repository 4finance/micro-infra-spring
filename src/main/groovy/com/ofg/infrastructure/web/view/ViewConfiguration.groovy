package com.ofg.infrastructure.web.view

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver

/**
 * Configuration that registers {@link JsonViewResolver} as a bean
 * 
 * @see JsonViewResolver
 */
@TypeChecked
@Configuration
class ViewConfiguration {

    @Bean
    ViewResolver viewResolver() {
        return new JsonViewResolver()
    }

}
