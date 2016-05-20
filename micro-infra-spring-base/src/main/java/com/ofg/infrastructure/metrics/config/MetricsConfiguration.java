package com.ofg.infrastructure.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.GraphiteUDP;
import com.codahale.metrics.graphite.PickledGraphite;
import com.codahale.metrics.jvm.*;
import com.google.common.collect.Iterables;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import com.ofg.infrastructure.metrics.publishing.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.ofg.config.BasicProfiles.DEVELOPMENT;
import static com.ofg.config.BasicProfiles.PRODUCTION;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Configuration that registers metric related Spring beans.
 * <p/>
 * For production use it registers publishing to JMX and Graphite.
 * For development use it only registers publishing to JMX.
 *
 * @see JmxPublisher
 * @see Graphite
 * @see GraphitePublisher
 * @see MetricRegistry
 * @see EnvironmentAwareMetricsBasePath
 */
@Configuration
@ConditionalOnProperty(prefix = "metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MetricsConfiguration {

    private static final Logger log = getLogger(lookup().lookupClass());

    @Autowired
    private Environment env;

    @Value("${metrics.jvm.path.base:jvm}")
    private String jvmMetricsPathBase;

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile({PRODUCTION, DEVELOPMENT})
    public JmxPublisher jmxPublisher(MetricRegistry metricRegistry) {
        return new JmxPublisher(metricRegistry, MINUTES, MILLISECONDS);
    }

    @Bean(destroyMethod = "close")
    @Profile(PRODUCTION)
    @Conditional(IsGraphitePublishingEnabled.class)
    public GraphiteSender graphite(@Value("${graphite.host:graphite.4finance.net}") String hostname, @Value("${graphite.port:2003}") int port, @Value("${graphite.format:UDP}") GraphiteFormat format) {
        final InetSocketAddress address = new InetSocketAddress(hostname, port);
        log.info("Configuring {} sender for Graphite server: {}", format, address);
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

    @Autowired(required = false) ServiceConfigurationResolver serviceConfigurationResolver;
    @Autowired(required = false) ZookeeperDiscoveryProperties zookeeperDiscoveryProperties;

    @Bean
    @Profile(PRODUCTION)
    public MetricsBasePath metricsBasePath(@Value("${metrics.path.root:apps}") String rootName,
                                           @Value("${metrics.path.environment:#{systemProperties['APP_ENV'] ?: 'test'}}") String environment,
                                           @Value("spring.application.name") String springAppName,
                                           @Value("${metrics.path.node:#{T(com.ofg.infrastructure.metrics.config.MetricsConfiguration).resolveLocalHostName()}}") String node) {
        String basePath = zookeeperDiscoveryProperties != null ? zookeeperDiscoveryProperties.getRoot() : serviceConfigurationResolver.getBasePath();
        String applicationName = zookeeperDiscoveryProperties != null ? getLastName(springAppName) : serviceConfigurationResolver.getMicroservicePath().getLastName();
        String country = env.getProperty("metrics.path.country", basePath);
        String appName = env.getProperty("metrics.path.app", applicationName);
        return new EnvironmentAwareMetricsBasePath(rootName, environment, country, appName, node);
    }

    private String getLastName(String path) {
        if (path == null) {
            return StringUtils.EMPTY;
        }
        return Iterables.getLast(Arrays.asList(path.split("/")));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(PRODUCTION)
    @Conditional(IsGraphitePublishingEnabled.class)
    public GraphitePublisher graphitePublisher(GraphiteSender graphite,
                                               MetricRegistry metricRegistry,
                                               @Value("${graphite.publishing.interval:15000}") long publishingIntervalInMs,
                                               MetricsBasePath metricsBasePath) {
        PublishingInterval publishingInterval = new PublishingInterval(publishingIntervalInMs, MILLISECONDS);
        return new GraphitePublisher(graphite, publishingInterval, metricRegistry, MINUTES, MILLISECONDS, metricsBasePath);
    }

    @Bean
    public MetricRegistry metricRegistry(@Value("${metrics.jvm.enabled:true}") boolean registerJvmMetrics) {
        MetricRegistry metricRegistry = new MetricRegistry();
        if (registerJvmMetrics) {
            registerJvmMetrics(metricRegistry);
        }
        return metricRegistry;
    }

    private void registerJvmMetrics(MetricRegistry metricRegistry) {
        metricRegistry.register(jvmMetricName("gc"), new GarbageCollectorMetricSet());
        metricRegistry.register(jvmMetricName("memory"), new MemoryUsageGaugeSet());
        metricRegistry.register(jvmMetricName("thread-states"), new ThreadStatesGaugeSet());
        metricRegistry.register(jvmMetricName("fd"), new FileDescriptorRatioGauge());
        metricRegistry.register(jvmMetricName("classloading"), new ClassLoadingGaugeSet());
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

    private String jvmMetricName(String metric) {
        return isEmpty(jvmMetricsPathBase) ? metric : jvmMetricsPathBase + '.' + metric;
    }
}