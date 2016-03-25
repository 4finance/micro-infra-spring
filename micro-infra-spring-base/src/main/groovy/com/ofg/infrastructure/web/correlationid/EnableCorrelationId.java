package com.ofg.infrastructure.web.correlationid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Enables support for correlation id.
 *
 * @see org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TraceAutoConfiguration.class)
public @interface EnableCorrelationId {

}
