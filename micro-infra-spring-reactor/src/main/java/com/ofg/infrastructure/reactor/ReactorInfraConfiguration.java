package com.ofg.infrastructure.reactor;

import com.ofg.infrastructure.reactor.aspect.ReactorAspectConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Base configuration for Spring Reactor. It takes care of importing all the configuration that are solving the following issues:
 * <ul>
 * <li>CorrelationId setting</li>
 * </ul>
 * <p/>
 * NOTE: CorrelationId setting works only for the Spring Reactor Java based approach!
 *
 * @see ReactorAspectConfiguration
 */
@Import(ReactorAspectConfiguration.class)
@Configuration
public class ReactorInfraConfiguration {
}
