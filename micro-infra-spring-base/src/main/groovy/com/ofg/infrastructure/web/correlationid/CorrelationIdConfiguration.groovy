package com.ofg.infrastructure.web.correlationid
import groovy.transform.TypeChecked
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Registers beans that add correlation id to requests
 * 
 * @see CorrelationIdAspect
 * @see CorrelationIdFilter
 */
@TypeChecked
@Configuration
class CorrelationIdConfiguration {

    @Bean
    CorrelationIdAspect correlationIdAspect() {
        return new CorrelationIdAspect()
    }

    @Bean
    FilterRegistrationBean correlationHeaderFilter() {
        return new FilterRegistrationBean(new CorrelationIdFilter())
    }

}
