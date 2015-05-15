package com.ofg.infrastructure.web.config;

import com.ofg.infrastructure.web.correlationid.CorrelationIdConfiguration;
import com.ofg.infrastructure.web.correlationid.EnableCorrelationId;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration;
import com.ofg.infrastructure.web.view.ViewConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration related to default web application setup. Imports:
 * <ul>
 * <li>{@link ServiceRestClientConfiguration} - RestTemplate abstraction with ServiceDiscovery</li>
 * <li>{@link CorrelationIdConfiguration} - adds correlation id to requests</li>
 * <li>{@link ViewConfiguration} - converts unmapped views to JSON requests</li>
 * </ul>
 *
 * @see ServiceRestClientConfiguration
 * @see CorrelationIdConfiguration
 * @see ViewConfiguration
 */
@Configuration
@EnableCorrelationId
@Import({ServiceRestClientConfiguration.class,  ViewConfiguration.class})
public class WebInfrastructureConfiguration {
}
