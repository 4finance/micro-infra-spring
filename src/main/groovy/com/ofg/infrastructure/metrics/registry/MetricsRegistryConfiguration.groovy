package com.ofg.infrastructure.metrics.registry

import com.codahale.metrics.MetricRegistry
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration that registers MetricRegistry as a Spring bean.
 * 
 * Example of core Metrics registrations
 *
 * <pre>
 *     @Configuration
 *     @Import(MetricsRegistryConfiguration.class)
 *     @Profile(Profiles.PRODUCTION)
 *     public class GraphitePublisherConfigration {
 *          @Autowired MetricRegistry metricsRegistry;
 *
 *          @Bean
 *          Graphite graphite(@Value("${graphite.host:graphite.4finance.net}") String hostname, @Value("${graphite.port:2003}") int port) {
 *               return new Graphite(new InetSocketAddress(hostname, port));
 *          }
 *
 *          @Bean(initMethod = "start", destroyMethod = "stop")
 *          GraphitePublisher graphitePublisher(Graphite graphite, MetricRegistry metricRegistry) {
 *          PublishingInterval publishingInterval = new PublishingInterval(15, SECONDS);
 *               return new GraphitePublisher(graphite, publishingInterval, metricRegistry, MINUTES, MILLISECONDS);
 *          }
 *     }
 * </pre>
 */
@Configuration
@TypeChecked
class MetricsRegistryConfiguration {

    @Bean
    MetricRegistry metricRegistry() {
        return new MetricRegistry()
    }
}
