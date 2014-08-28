package com.ofg.infrastructure.web.resttemplate

import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Contains default configuration related to RestTemplate {@link org.springframework.web.client.RestTemplate}
 *
 * @see org.springframework.web.client.RestTemplate
 */
@TypeChecked
@Configuration
class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate()
    }

}
