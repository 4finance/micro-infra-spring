package com.ofg.infrastructure.config;

import com.ofg.infrastructure.discovery.EnableServiceDiscovery;
import com.ofg.infrastructure.healthcheck.EnableHealthCheck;
import com.ofg.infrastructure.metrics.config.EnableMetrics;
import com.ofg.infrastructure.tracing.EnableTracing;
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration that gives you the full stack of microservice infrastructure. Below you can find a list of
 * imported configurations and their purpose
 * <p/>
 * Imports:
 * <ul>
 * <li>{@link WebInfrastructureConfiguration} - contains configurations related to filter, service communication and web application setup</li>
 * </ul>
 * <p/>
 * Enables:
 * <ul>
 * <lI>{@link com.ofg.infrastructure.discovery.EnableServiceDiscovery}</lI>
 * <lI>{@link com.ofg.infrastructure.metrics.config.EnableMetrics}</lI>
 * <lI>{@link com.ofg.infrastructure.healthcheck.EnableHealthCheck}</lI>
 * </ul>
 *
 * @see WebInfrastructureConfiguration
 * @see EnableServiceDiscovery
 * @see EnableMetrics
 * @see EnableHealthCheck
 */

@Configuration
@EnableHealthCheck
@EnableMetrics
@EnableTracing
@Import(WebInfrastructureConfiguration.class)
public class BaseWebAppConfiguration {
}
