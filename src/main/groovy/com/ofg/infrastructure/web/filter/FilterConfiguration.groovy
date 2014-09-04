package com.ofg.infrastructure.web.filter

import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdConfiguration
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

import javax.servlet.Filter

/**
 * Configuration that imports {@link CorrelationIdConfiguration} and registers a {@link RequestBodyLoggingContextFilter}.
 * 
 * That way your application will automatically 
 * <ul>
 *     <li>set correlation id on received and sent request</li>
 *     <li>if DEUBG level of logging is set on {@link RequestBodyLoggingContextFilter} 
 *     print request body in logs - you can limit its length by setting a property</li>
 * </ul>
 * 
 * @see CorrelationIdConfiguration
 * @see RequestBodyLoggingContextFilter
 */
@TypeChecked
@Configuration
@Import(CorrelationIdConfiguration)
class FilterConfiguration {

    @Bean
    Filter requestBodyLoggingContextFilter(@Value('${request.payload.logging.maxlength:2000}') int maxPayloadLength) {
        return new RequestBodyLoggingContextFilter(maxPayloadLength)
    }

}
