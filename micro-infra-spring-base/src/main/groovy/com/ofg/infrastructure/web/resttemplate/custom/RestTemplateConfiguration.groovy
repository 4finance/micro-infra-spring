package com.ofg.infrastructure.web.resttemplate.custom

import com.ofg.infrastructure.MicroInfraSpringQualifier
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations

/**
 * Contains default configuration related to {@link RestOperations}
 *
 * @see org.springframework.web.client.RestTemplate
 * @see RestOperations
 */
@TypeChecked
@Configuration
class RestTemplateConfiguration {

    @Bean
    @Qualifier(MicroInfraSpringQualifier.VALUE)
    RestOperations restTemplate() {
        return new RestTemplate()
    }

}
