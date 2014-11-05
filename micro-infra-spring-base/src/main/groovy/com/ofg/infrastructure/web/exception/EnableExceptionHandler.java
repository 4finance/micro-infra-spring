package com.ofg.infrastructure.web.exception;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for controller exception handling.
 *
 * @see com.ofg.infrastructure.web.exception.ControllerExceptionConfiguration
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ControllerExceptionConfiguration.class)
public @interface EnableExceptionHandler {

}
