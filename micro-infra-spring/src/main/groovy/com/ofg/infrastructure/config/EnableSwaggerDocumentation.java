package com.ofg.infrastructure.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for Swagger API Documentation.
 *
 * Imports:
 * <ul>
 *  <li>{@link com.ofg.infrastructure.config.SwaggerDocumentationConfiguration} - contains configurations related to Swagger API documentation
 * </ul>
 *
 * @see BaseWebAppConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SwaggerDocumentationConfiguration.class)
public @interface EnableSwaggerDocumentation {

}
