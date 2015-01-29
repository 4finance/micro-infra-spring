package com.ofg.infrastructure.metrics.config

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteSender
import com.codahale.metrics.graphite.GraphiteUDP
import com.codahale.metrics.graphite.PickledGraphite
import com.codahale.metrics.jvm.ClassLoadingGaugeSet
import com.codahale.metrics.jvm.FileDescriptorRatioGauge
import com.codahale.metrics.jvm.GarbageCollectorMetricSet
import com.codahale.metrics.jvm.MemoryUsageGaugeSet
import com.codahale.metrics.jvm.ThreadStatesGaugeSet
import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.metrics.publishing.GraphitePublisher
import com.ofg.infrastructure.metrics.publishing.JmxPublisher
import com.ofg.infrastructure.metrics.publishing.PublishingInterval
import com.ofg.infrastructure.metrics.registry.MetricPathProvider
import com.ofg.infrastructure.metrics.registry.PathPrependingMetricRegistry
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import static com.ofg.infrastructure.metrics.config.GraphiteFormat.PICKLE
import static com.ofg.infrastructure.metrics.config.GraphiteFormat.TCP
import static com.ofg.infrastructure.metrics.config.GraphiteFormat.UDP
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
@Slf4j
class MetricsConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile([BasicProfiles.PRODUCTION, BasicProfiles.DEVELOPMENT, BasicProfiles.TEST])
    JmxPublisher jmxPublisher(MetricRegistry metricRegistry) {
        return new JmxPublisher(metricRegistry, MINUTES, MILLISECONDS)
    }

    @Bean(destroyMethod = "close")
    @Profile(BasicProfiles.PRODUCTION)
    @Conditional(IsGraphitePublishingEnabled)
    GraphiteSender graphite(@Value('${graphite.host:graphite.4finance.net}') String hostname,
                      @Value('${graphite.port:2003}') int port,
                      @Value('${graphite.format:TCP}') GraphiteFormat format) {
        final InetSocketAddress address = new InetSocketAddress(hostname, port)
        log.info("Connecting to Graphite $address using $format format")
        switch (format) {
            case UDP:
                return new GraphiteUDP(address)
            case TCP:
                return new Graphite(address)
            case PICKLE:
                return new PickledGraphite(address)
            default:
                throw new IllegalArgumentException("Unexpected graphite.format value: $format. Expected values are: ${GraphiteFormat.values()}")
        }
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(BasicProfiles.PRODUCTION)
    @Conditional(IsGraphitePublishingEnabled)
    GraphitePublisher graphitePublisher(GraphiteSender graphite,
                                        MetricRegistry metricRegistry,
                                        @Value('${graphite.publishing.interval:15000}') long publishingIntervalInMs) {
        PublishingInterval publishingInterval = new PublishingInterval(publishingIntervalInMs, MILLISECONDS)
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
        MetricRegistry metricRegistry = new PathPrependingMetricRegistry(metricPathProvider)
        metricRegistry.register(MetricRegistry.name("jvm", "gc"), new GarbageCollectorMetricSet());
        metricRegistry.register(MetricRegistry.name("jvm", "memory"), new MemoryUsageGaugeSet());
        metricRegistry.register(MetricRegistry.name("jvm", "thread-states"), new ThreadStatesGaugeSet());
        metricRegistry.register(MetricRegistry.name("jvm", "fd"), new FileDescriptorRatioGauge());
        metricRegistry.register(MetricRegistry.name("jvm", "classloading"), new ClassLoadingGaugeSet());
        return metricRegistry;
    }
}
