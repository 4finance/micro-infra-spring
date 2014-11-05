package com.ofg.infrastructure.reactor.aspect;

import com.ofg.infrastructure.web.logging.RequestLoggingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for {@link ReactorAspect}.
 *
 * That way, combined with {@link com.ofg.infrastructure.reactor.event.ReactorEvent} and
 * {@link reactor.spring.annotation.Selector} annotations correlationId will be set for
 * applications using Spring Reactor.
 *
 * @see ReactorAspect
 * @see ReactorAspectConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ReactorAspectConfiguration.class)
public @interface EnableReactorAspect {

}
