package com.ofg.infrastructure.hystrix;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for a servlet emitting Hystrix data
 *
 * @see com.ofg.infrastructure.hystrix.HystrixServletConfiguration
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HystrixServletConfiguration.class)
public @interface EnableHystrixServlet {

}
