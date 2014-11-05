package com.ofg.infrastructure.web.view;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables JSON serialization for objects returned by controllers' methods.
 *
 * Pretty printing setting is based on active profile:
 * - in production environment pretty printing is set to false,
 * - in test or development environment pretty printing is set to true.
 *
 * @see com.ofg.infrastructure.web.view.ViewConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ViewConfiguration.class)
public @interface EnableJSONViewResolver {

}
