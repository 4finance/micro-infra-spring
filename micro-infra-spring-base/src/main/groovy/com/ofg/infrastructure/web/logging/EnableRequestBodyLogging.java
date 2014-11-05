package com.ofg.infrastructure.web.logging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for request body logging.
 * <p/>
 * Configuration that registers a bean that will automatically if DEBUG level of logging is set on
 * {@link com.ofg.infrastructure.web.logging.RequestBodyLoggingContextFilter}
 * print request body in logs - you can limit its length by setting a property
 *
 * @see com.ofg.infrastructure.web.logging.RequestLoggingConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RequestLoggingConfiguration.class)
public @interface EnableRequestBodyLogging {

}
