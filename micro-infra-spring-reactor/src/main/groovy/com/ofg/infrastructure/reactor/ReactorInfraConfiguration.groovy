package com.ofg.infrastructure.reactor

import com.ofg.infrastructure.reactor.aspect.ReactorAspectConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Base configuration for Spring Reactor. It takes care of importing all the configuration that are solving the following issues:
 * <ul>
 *      <li>CorrelationId setting</li>
 * </ul>
 *
 * NOTE: CorrelationId setting works only for the Spring Reactor Java based approach!
 *
 * @see ReactorAspectConfiguration
 */
@CompileStatic
@Import([ReactorAspectConfiguration])
@Configuration
class ReactorInfraConfiguration {
}
