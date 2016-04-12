package com.ofg.infrastructure.web.config;

import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ofg.infrastructure.discovery.EnableServiceDiscovery;
import com.ofg.infrastructure.tracing.EnableTracing;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration;
import com.ofg.infrastructure.web.view.ViewConfiguration;

/**
 * Configuration related to default web application setup. Imports:
 * <ul>
 * <li>{@link ServiceRestClientConfiguration} - RestTemplate abstraction with ServiceDiscovery</li>
 * <li>{@link TraceAutoConfiguration} - adds correlation id to requests</li>
 * <li>{@link ViewConfiguration} - converts unmapped views to JSON requests</li>
 * </ul>
 *
 * @see ServiceRestClientConfiguration
 * @see TraceAutoConfiguration
 * @see ViewConfiguration
 */
@Configuration
@EnableServiceDiscovery
@EnableTracing
@Import({ServiceRestClientConfiguration.class,  ViewConfiguration.class})
public class WebInfrastructureConfiguration {
}
