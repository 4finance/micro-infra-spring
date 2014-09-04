package com.ofg.infrastructure.web.config

import com.ofg.infrastructure.web.exception.ControllerExceptionConfiguration
import com.ofg.infrastructure.web.filter.FilterConfiguration
import com.ofg.infrastructure.web.resttemplate.RestTemplateConfiguration
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration
import com.ofg.infrastructure.web.view.ViewConfiguration
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration related to default web application setup. Imports:
 *  <li>
 *      <ul>
 *          <li>{@link RestTemplateConfiguration} - default RestTemplate with custom error handling</li>
 *          <li>{@link ServiceRestClientConfiguration} - RestTemplate abstraction with ServiceDiscovery</li>
 *          <li>{@link ControllerExceptionConfiguration} - default Exception handling</li>
 *          <li>{@link FilterConfiguration} - filter for logging request body</li>
 *          <li>{@link ViewConfiguration} - converts unmapped views to JSON requests</li>
 *      </ul>
 *  </li>
 *
 * @see RestTemplateConfiguration
 * @see ServiceRestClientConfiguration
 * @see ControllerExceptionConfiguration
 * @see FilterConfiguration
 * @see ViewConfiguration
 */
@Configuration
@TypeChecked
@Import([RestTemplateConfiguration,
        ServiceRestClientConfiguration,
        ControllerExceptionConfiguration,
        FilterConfiguration,
        ViewConfiguration])
class WebInfrastructureConfiguration {

}
