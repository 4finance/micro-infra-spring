package com.ofg.infrastructure.metrics.config

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.Graphite
import com.ofg.infrastructure.metrics.publishing.GraphitePublisher
import com.ofg.infrastructure.metrics.publishing.JmxPublisher
import com.ofg.infrastructure.metrics.registry.MetricPathProvider
import com.ofg.infrastructure.metrics.registry.PathPrependingMetricRegistry
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import static com.ofg.config.BasicProfiles.*
import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.MINUTES

/**
 * Configuration that registers metric related Spring beans. 
 *
 * For production use it registers publishing to JMX and Graphite.
 * For development use it only registers publishing to JMX.
 *
 * @see JmxPublisher
 * @see Graphite
 * @see GraphitePublisher
 * @see MetricPathProvider
 * @see MetricRegistry
 */
@Configuration
@CompileStatic
class MetricsConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile([PRODUCTION, DEVELOPMENT, TEST])
    JmxPublisher jmxPublisher(MetricRegistry metricRegistry) {
        return new JmxPublisher(metricRegistry, MINUTES, MILLISECONDS)
    }

    @Bean
    @Profile(PRODUCTION)
    Graphite graphite(@Value('${graphite.host:graphite.4finance.net}') String hostname, @Value('${graphite.port:2003}') int port) {
        return new Graphite(new InetSocketAddress(hostname, port))
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(PRODUCTION)
    GraphitePublisher graphitePublisher(Graphite graphite, MetricRegistry metricRegistry,
                                        @Value('${graphite.publishing.interval:15000}') long publishingIntervalInMs) {
        GraphitePublisher.PublishingInterval publishingInterval = new GraphitePublisher.PublishingInterval(publishingIntervalInMs, MILLISECONDS)
        return new GraphitePublisher(graphite, publishingInterval, metricRegistry, MINUTES, MILLISECONDS)
    }

    @Bean
    MetricPathProvider metricPathProvider(@Value('${metrics.path.root:apps}') String rootName,
                                          @Value('${metrics.path.environment:test}') String environment,
                                          @Value('${metrics.path.country:pl}') String country,
                                          @Value('${metrics.path.app:service-name}') String appName) {
        return new MetricPathProvider(rootName, environment, country, appName)
    }

    @Bean
    MetricRegistry metricRegistry(MetricPathProvider metricPathProvider) {
        return new PathPrependingMetricRegistry(metricPathProvider)
    }
}
