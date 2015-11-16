package com.ofg.infrastructure.tracing;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for the tracing modules basing on Spring Cloud Sleuth.
 *
 * Imports:
 * <ul>
 *  <li>{@link com.ofg.infrastructure.tracing.TracingConfiguration} - contains configurations related to tracing</li>
 * </ul>
 *
 * @see com.ofg.infrastructure.tracing.TracingConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TracingConfiguration.class)
public @interface EnableTracing {
}
