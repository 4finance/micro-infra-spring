package com.ofg.infrastructure.web.filter.logging

import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.servlet.Filter
/**
 * Configuration that registers a bean that will automatically if DEUBG level of logging is set on 
 * {@link com.ofg.infrastructure.web.filter.logging.RequestBodyLoggingContextFilter} 
 * print request body in logs - you can limit its length by setting a property
 * 
 * @see com.ofg.infrastructure.web.filter.logging.RequestBodyLoggingContextFilter
 */
@TypeChecked
@Configuration
class RequestFilterConfiguration {

    @Bean
    Filter requestBodyLoggingContextFilter(@Value('${request.payload.logging.maxlength:2000}') int maxPayloadLength) {
        return new RequestBodyLoggingContextFilter(maxPayloadLength)
    }

}
