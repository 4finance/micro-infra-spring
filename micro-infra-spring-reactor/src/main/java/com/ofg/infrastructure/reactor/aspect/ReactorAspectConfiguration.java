package com.ofg.infrastructure.reactor.aspect;

import com.ofg.infrastructure.reactor.event.ReactorEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.spring.annotation.Selector;

/**
 * Configuration that registers {@link ReactorAspect} as a Spring bean.
 * That way, combined with {@link ReactorEvent} and {@link Selector}
 * annotations correlationId will be set for applications using Spring Reactor.
 *
 * @see ReactorAspect
 */
@Configuration
public class ReactorAspectConfiguration {
    @Bean
    public ReactorAspect reactorAspect() {
        return new ReactorAspect();
    }

}
