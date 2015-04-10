package com.ofg.infrastructure.metrics.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for registering metric related Spring beans.
 *
 * For production use it registers publishing to JMX and Graphite.
 * For development use it only registers publishing to JMX.
 *
 * @see com.ofg.infrastructure.metrics.config.MetricsConfiguration
 * @see com.codahale.metrics.MetricRegistry
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MetricsConfiguration.class)
public @interface EnableMetrics {

}
