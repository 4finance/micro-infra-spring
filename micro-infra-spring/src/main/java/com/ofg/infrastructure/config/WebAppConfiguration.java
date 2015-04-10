package com.ofg.infrastructure.config;

import com.ofg.infrastructure.web.swagger.SwaggerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that gives you the full stack of microservice infrastructure with Swagger API documentation.
 * Below you can find a list of imported configurations and their purpose
 * <p/>
 * Imports:
 * <ul>
 * <li>{@link com.ofg.infrastructure.config.BaseWebAppConfiguration} - contains base configurations with service discovery, Spring environment checks, metrics, correlationId etc.</li>
 * <li>{@link SwaggerConfiguration} - contains configurations related to Swagger API documentation
 * </ul>
 *
 * @see com.ofg.infrastructure.config.BaseWebAppConfiguration
 * @deprecated Use {@link com.ofg.infrastructure.config.EnableMicroserviceDocumentation} with
 * {@link com.ofg.infrastructure.config.EnableMicroservice} instead.
 */
@Configuration
@Import({BaseWebAppConfiguration.class, SwaggerConfiguration.class})
@Deprecated
public class WebAppConfiguration {
}
