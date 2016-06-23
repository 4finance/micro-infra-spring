package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import com.ofg.config.NotSpringCloudProfile;
import com.ofg.infrastructure.discovery.ZookeeperConnectorConditions.InMemoryZookeeperCondition;
import com.ofg.infrastructure.discovery.ZookeeperConnectorConditions.StandaloneZookeeperCondition;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import static java.lang.invoke.MethodHandles.lookup;

import java.util.Map;

/**
 * Class holding configuration to Zookeeper server, Zookeeper service instance and to Curator framework.
 * <p/>
 * All the beans are available only in the {@link BasicProfiles#PRODUCTION} profile.
 *
 * @see CuratorFramework
 * @see RetryPolicy
 * @see ServiceInstance
 * @see ServiceDiscovery
 */
@Import(ConsumerDrivenContractConfiguration.class)
@Configuration
@Deprecated
@NotSpringCloudProfile
public class ServiceDiscoveryInfrastructureConfiguration {

    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @Bean
    public RetryPolicy exponentialBackoffRetry(@Value("${service.resolver.connection.retry.baseSleepMs:50}") int numberOfRetries,
                                               @Value("${service.resolver.connection.retry.maxRetries:20}") int maxRetries,
                                               @Value("${service.resolver.connection.retry.maxSleepMs:500}") int maxSleepMs) {
        return new ExponentialBackoffRetry(numberOfRetries, maxRetries, maxSleepMs);
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework(ZookeeperConnector zookeeperConnector, RetryPolicy retryPolicy) {
        return CuratorFrameworkFactory.newClient(zookeeperConnector.getServiceResolverUrl(), retryPolicy);
    }

    @Bean
    @Conditional(StandaloneZookeeperCondition.class)
    public ZookeeperConnector standaloneZookeeperConnector(@Value("${service.resolver.url:localhost:2181}") final String serviceResolverUrl) {
        return new ZookeeperConnector() {
            @Override
            public String getServiceResolverUrl() {
                return serviceResolverUrl;
            }

        };
    }

    @Bean
    @Conditional(InMemoryZookeeperCondition.class)
    public ZookeeperConnector inMemoryZookeeperConnector(final TestingServer testingServer) {
        return new ZookeeperConnector() {
            @Override
            public String getServiceResolverUrl() {
                return testingServer.getConnectString();
            }

        };
    }

    @Bean
    public ServiceInstance serviceInstance(MicroserviceAddressProvider addressProvider,
                                           ServiceConfigurationResolver serviceConfigurationResolver) throws Exception {
        return ServiceInstance
                .builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address(addressProvider.getHost())
                .port(addressProvider.getPort())
                .name(serviceConfigurationResolver.getMicroservicePath().getPath())
                .build();
    }

    @Bean
    public InstanceSerializer instanceSerializer() {
        return new IgnorePayloadInstanceSerializer(Map.class);
    }

    @Bean(destroyMethod = "close")
    @SuppressWarnings("unchecked")
    public ServiceDiscovery serviceDiscovery(CuratorFramework curatorFramework,
                                             ServiceInstance serviceInstance,
                                             ServiceConfigurationResolver serviceConfigurationResolver,
                                             InstanceSerializer instanceSerializer) {
        log.info("Registering myself: " + String.valueOf(serviceInstance));
        final ServiceDiscovery<Map> serviceDiscovery = ServiceDiscoveryBuilder
                .builder(Map.class)
                .basePath("/" + serviceConfigurationResolver.getBasePath())
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .serializer(instanceSerializer)
                .build();
        asyncRegisterInsideZookeeper(serviceDiscovery);
        return serviceDiscovery;
    }

    private void asyncRegisterInsideZookeeper(final ServiceDiscovery<Map> serviceDiscovery) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serviceDiscovery.start();
                    log.info("Registration inside zookeeper successful");
                } catch (Exception e) {
                    log.error("Error during service registration inside Zookeeper");
                    System.exit(1);
                }
            }
        }, "async-register-service-thread").start();
    }

}
