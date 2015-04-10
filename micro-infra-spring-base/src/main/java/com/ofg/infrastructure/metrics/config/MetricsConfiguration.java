package com.ofg.infrastructure.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.GraphiteUDP;
import com.codahale.metrics.graphite.PickledGraphite;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.ofg.config.BasicProfiles;
import com.ofg.infrastructure.metrics.publishing.GraphitePublisher;
import com.ofg.infrastructure.metrics.publishing.JmxPublisher;
import com.ofg.infrastructure.metrics.publishing.PublishingInterval;
import com.ofg.infrastructure.metrics.registry.MetricPathProvider;
import com.ofg.infrastructure.metrics.registry.PathPrependingMetricRegistry;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configuration that registers metric related Spring beans.
 * <p/>
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
@Slf4j
public class MetricsConfiguration {

    private static final Logger log = getLogger(lookup().lookupClass());

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(value = {BasicProfiles.PRODUCTION, BasicProfiles.DEVELOPMENT})
    public JmxPublisher jmxPublisher(MetricRegistry metricRegistry) {
        return new JmxPublisher(metricRegistry, MINUTES, MILLISECONDS);
    }

    @Bean(destroyMethod = "close")
    @Profile(BasicProfiles.PRODUCTION)
    @Conditional(IsGraphitePublishingEnabled.class)
    public GraphiteSender graphite(@Value("${graphite.host:graphite.4finance.net}") String hostname, @Value("${graphite.port:2003}") int port, @Value("${graphite.format:TCP}") GraphiteFormat format) {
        final InetSocketAddress address = new InetSocketAddress(hostname, port);
        log.info("Connecting to Graphite " + String.valueOf(address) + " using " + String.valueOf(format) + " format");
        switch (format) {
            case UDP:
                return new GraphiteUDP(address);
            case TCP:
                return new Graphite(address);
            case PICKLE:
                return new PickledGraphite(address);
            default:
                throw new IllegalArgumentException("Unexpected graphite.format value: " + String.valueOf(format) + ". Expected values are: " + String.valueOf(GraphiteFormat.values()));
        }
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(BasicProfiles.PRODUCTION)
    @Conditional(IsGraphitePublishingEnabled.class)
    public GraphitePublisher graphitePublisher(GraphiteSender graphite,
                                               MetricRegistry metricRegistry,
                                               @Value("${graphite.publishing.interval:15000}") long publishingIntervalInMs) {
        PublishingInterval publishingInterval = new PublishingInterval(publishingIntervalInMs, MILLISECONDS);
        return new GraphitePublisher(graphite, publishingInterval, metricRegistry, MINUTES, MILLISECONDS);
    }

    @Bean
    public MetricPathProvider metricPathProvider(@Value("${metrics.path.root:apps}") String rootName,
                                                 @Value("${metrics.path.environment:test}") String environment,
                                                 @Value("${metrics.path.country:pl}") String country,
                                                 @Value("${metrics.path.app:service-name}") String appName,
                                                 @Value("${metrics.path.node:#{T(com.ofg.infrastructure.metrics.config.MetricsConfiguration).resolveLocalHostName()}}") String node) {
        return new MetricPathProvider(rootName, environment, country, appName, node);
    }

    @Bean
    public MetricRegistry metricRegistry(MetricPathProvider metricPathProvider) {
        MetricRegistry metricRegistry = new PathPrependingMetricRegistry(metricPathProvider);
        metricRegistry.register(MetricRegistry.name("jvm", "gc"), new GarbageCollectorMetricSet());
        metricRegistry.register(MetricRegistry.name("jvm", "memory"), new MemoryUsageGaugeSet());
        metricRegistry.register(MetricRegistry.name("jvm", "thread-states"), new ThreadStatesGaugeSet());
        metricRegistry.register(MetricRegistry.name("jvm", "fd"), new FileDescriptorRatioGauge());
        metricRegistry.register(MetricRegistry.name("jvm", "classloading"), new ClassLoadingGaugeSet());
        return metricRegistry;
    }

    @Bean
    public HystrixCodaHaleMetricsPublisher hystrixCodaHaleMetricsPublisher(MetricRegistry metricRegistry) {
        HystrixCodaHaleMetricsPublisher publisher = new HystrixCodaHaleMetricsPublisher(metricRegistry);
        HystrixPlugins.reset();
        HystrixPlugins.getInstance().registerMetricsPublisher(publisher);
        return publisher;
    }

    public static String resolveLocalHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }
}