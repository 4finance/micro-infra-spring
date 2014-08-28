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
 * Imports:
 *  <li>
 *      <ul>
 *          <li>RestTemplateConfiguration - default RestTemplate with custom error handling</li>
 *          <li>ServiceRestClientConfiguration - RestTemplate abstraction with ServiceDiscovery</li>
 *          <li>ControllerExceptionConfiguration - default Exception handling</li>
 *          <li>FilterConfiguration - filter for logging request body</li>
 *          <li>ViewConfiguration - converts unmapped Views to JSON requests</li>
 *      </ul>
 *  </li>
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
