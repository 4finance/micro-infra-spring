package com.ofg.infrastructure.web.resttemplate

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@TypeChecked
@Configuration
class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate()
    }

}
