package com.ofg.infrastructure.base

import groovy.transform.TypeChecked
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@TypeChecked
@EnableAutoConfiguration
class BaseConfiguration {
    
    @Bean
    static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer()
    }
    
}
