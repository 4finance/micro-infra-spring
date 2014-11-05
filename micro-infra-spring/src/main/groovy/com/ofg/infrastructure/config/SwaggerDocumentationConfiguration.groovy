package com.ofg.infrastructure.config

import com.ofg.infrastructure.web.swagger.SwaggerConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
/**
 * Configuration that gives you the full stack of microservice infrastructure with Swagger API documentation.
 * Below you can find a list of imported configurations and their purpose.
 *
 * Imports:
 * <ul>
 *  <li>{@link SwaggerConfiguration} - contains configurations related to Swagger API documentation
 * </ul>
 *
 * @see SwaggerConfiguration
 */
@Configuration
@CompileStatic
@Import([SwaggerConfiguration])
class SwaggerDocumentationConfiguration {

}
