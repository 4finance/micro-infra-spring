package com.ofg.infrastructure.config;

import com.ofg.infrastructure.web.swagger.SwaggerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for Swagger API Documentation.
 *
 * @see SwaggerConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SwaggerConfiguration.class)
public @interface EnableMicroserviceDocumentation {

}
