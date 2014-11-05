package com.ofg.infrastructure.web.correlationid;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for correlation id.
 *
 * @see com.ofg.infrastructure.web.correlationid.CorrelationIdConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CorrelationIdConfiguration.class)
public @interface EnableCorrelationId {

}
