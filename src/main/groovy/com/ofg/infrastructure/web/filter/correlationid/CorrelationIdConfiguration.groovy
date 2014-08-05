package com.ofg.infrastructure.web.filter.correlationid

import groovy.transform.TypeChecked
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
