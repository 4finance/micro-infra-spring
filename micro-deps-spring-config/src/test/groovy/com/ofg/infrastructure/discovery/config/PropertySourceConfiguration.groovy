package com.ofg.infrastructure.discovery.config

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@TypeChecked
@Configuration
class PropertySourceConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesConfigurer() {
        return new PropertySourcesPlaceholderConfigurer()
    }
}
