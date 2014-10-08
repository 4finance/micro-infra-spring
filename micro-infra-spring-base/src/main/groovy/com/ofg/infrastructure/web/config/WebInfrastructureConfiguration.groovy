package com.ofg.infrastructure.web.config

import com.ofg.infrastructure.web.filter.FilterConfiguration
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
 *          <li>{@link FilterConfiguration} - filter for logging request body</li>
 *          <li>{@link ViewConfiguration} - converts unmapped views to JSON requests</li>
 *      </ul>
 *  </li>
 *
 * @see ServiceRestClientConfiguration
 * @see FilterConfiguration
 * @see ViewConfiguration
 */
@Configuration
@CompileStatic
@Import([ServiceRestClientConfiguration,
        FilterConfiguration,
        ViewConfiguration])
class WebInfrastructureConfiguration {

}
