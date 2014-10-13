package com.ofg.infrastructure.web.config

import com.ofg.infrastructure.web.correlationid.CorrelationIdConfiguration
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration
import com.ofg.infrastructure.web.view.ViewConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration related to default web application setup. Imports:
 *  <li>
 *      <ul>
 *          <li>{@link ServiceRestClientConfiguration} - RestTemplate abstraction with ServiceDiscovery</li>
 *          <li>{@link CorrelationIdConfiguration} - adds correlation id to requests</li>
 *          <li>{@link ViewConfiguration} - converts unmapped views to JSON requests</li>
 *      </ul>
 *  </li>
 *
 * @see ServiceRestClientConfiguration
 * @see CorrelationIdConfiguration
 * @see ViewConfiguration
 */
@Configuration
@CompileStatic
@Import([ServiceRestClientConfiguration,
        CorrelationIdConfiguration,
        ViewConfiguration])
class WebInfrastructureConfiguration {

}
