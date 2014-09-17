package com.ofg.infrastructure.reactor

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration that registers {@link ReactorAspect} as a Spring bean.
 * That way, combined with {@link ReactorEvent} and {@link reactor.spring.annotation.Selector}
 * annotations correlationId will be set for applications using Spring Reactor.
 *
 * @see ReactorAspect
 */
@CompileStatic
@Configuration
class ReactorAspectConfiguration {

    @Bean ReactorAspect reactorAspect() {
        return new ReactorAspect()
    }
}
