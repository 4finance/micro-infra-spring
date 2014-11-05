package com.ofg.infrastructure.web.resttemplate.fluent;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for ServiceRestClient that will take care of everything for you and gives you a fluent interface to
 * achieve that.
 *
 * Creates a bean of abstraction over {@link org.springframework.web.client.RestOperations}.
 *
 * @see ServiceRestClient
 * @see ServiceRestClientConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ServiceRestClientConfiguration.class)
public @interface EnableServiceRestClient {

}
