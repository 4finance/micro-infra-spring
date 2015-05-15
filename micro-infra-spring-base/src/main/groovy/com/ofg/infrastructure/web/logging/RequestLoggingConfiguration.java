package com.ofg.infrastructure.web.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Configuration that registers a bean that will automatically if DEBUG level of logging is set on
 * {@link RequestBodyLoggingContextFilter}
 * print request body in logs - you can limit its length by setting a property
 *
 * @see RequestBodyLoggingContextFilter
 */
@Configuration
public class RequestLoggingConfiguration {
    @Bean
    public Filter requestBodyLoggingContextFilter(@Value("${request.payload.logging.maxlength:2000}") int maxPayloadLength) {
        return new RequestBodyLoggingContextFilter(maxPayloadLength);
    }

}
