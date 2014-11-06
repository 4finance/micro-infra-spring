package com.ofg.infrastructure.camel;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Enables Correlation Id for Camel.
 *
 * @see com.ofg.infrastructure.camel.aspects.CorrelationIdOnCamelRouteAspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CorrelationIdOnCamelRouteConfiguration.class)
public @interface EnableCorrelationIdForCamel {

}
