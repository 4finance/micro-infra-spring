package com.ofg.infrastructure.web.correlationid

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.util.regex.Pattern

/**
 * Registers beans that add correlation id to requests
 * 
 * @see CorrelationIdAspect
 * @see CorrelationIdFilter
 */
@Configuration
@CompileStatic
class CorrelationIdConfiguration {

    @Value('${rest.correlationId.skipPattern:#{T(com.ofg.infrastructure.web.correlationid.CorrelationIdFilter).DEFAULT_SKIP_PATTERN}}')
    private Pattern skipPattern

    @Bean
    CorrelationIdAspect correlationIdAspect() {
        return new CorrelationIdAspect()
    }

    @Bean
    FilterRegistrationBean correlationHeaderFilter() {
        return new FilterRegistrationBean(new CorrelationIdFilter(skipPattern))
    }
}
