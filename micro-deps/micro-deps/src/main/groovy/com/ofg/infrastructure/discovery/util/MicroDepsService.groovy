package com.ofg.infrastructure.discovery.util

import com.ofg.infrastructure.discovery.InstanceDetails
import com.google.common.annotations.VisibleForTesting
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.ZookeeperServiceResolver
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.DependencyWatcherListener
import com.ofg.infrastructure.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier
import groovy.transform.TypeChecked
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.*

/**
 * Class that registers microservice in Zookeeper server and enables its discovery using Curator framework.
 */
@TypeChecked
class MicroDepsService {
    private static final RetryPolicy DEFAULT_RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500)

    private final ServiceConfigurationResolver configurationResolver
    private final DependencyWatcher dependencyWatcher
    private final CuratorFramework curatorFramework
    private final ServiceInstance serviceInstance
    private final ServiceDiscovery serviceDiscovery
    @VisibleForTesting final ServiceResolver serviceResolver

    /**
     * Creates new instance of the class and registers microservice based on provided {@code microserviceConfig} in Zookeepaer server located at {@code zookeperUrl}.
     *
     * @param zookeeperUrl URL to running Zookeeper instance
     * @param microserviceContext registration context of microservice
     * @param microserviceUrl address of the microservice
     * @param microservicePort port of the microservice
     * @param microserviceConfig configuration of microservice, by default provided from {@code microservice.json} file placed on classpath
     * @param uriSpec specification of connection URI to microservice, by default {@code http://microserviceUrl:microservicePort/microserviceContext} is used
     * @param retryPolicy retry policy to connect to Zookeeper server, by default {@code ExponentialBackoffRetry} is used
     */
    MicroDepsService(String zookeeperUrl,
                     String microserviceContext,
                     String microserviceUrl,
                     int microservicePort,
                     String microserviceConfig = MicroDepsService.class.getResourceAsStream("/microservice.json").text,
                     String uriSpec = "{scheme}://{address}:{port}/$microserviceContext",
                     RetryPolicy retryPolicy = DEFAULT_RETRY_POLICY) {
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperUrl, retryPolicy)
        configurationResolver = new ServiceConfigurationResolver(microserviceConfig)
        serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec(uriSpec))
                .address(microserviceUrl)
                .port(microservicePort)
                .name(configurationResolver.microserviceName)
                .payload(instanceDetails())
                .build()
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(InstanceDetails)
                .basePath(configurationResolver.basePath)
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
        dependencyWatcher = new DependencyWatcher(configurationResolver.dependencies, serviceDiscovery, new DefaultDependencyPresenceOnStartupVerifier())
        serviceResolver = new ZookeeperServiceResolver(configurationResolver, serviceDiscovery, curatorFramework, new ProviderStrategyFactory())
    }

    private InstanceDetails instanceDetails() {
        List<String> dependenciesList = configurationResolver.dependencies.collect { entry ->
            entry.value[PATH] as String
        }
        return new InstanceDetails(dependenciesList)
    }

    void registerDependencyStateChangeListener(DependencyWatcherListener listener) {
        dependencyWatcher.registerDependencyStateChangeListener(listener)
    }

    MicroDepsService start() {
        curatorFramework.start()
        serviceDiscovery.start()
        dependencyWatcher.registerDependencies()
        serviceResolver.start()
        return this
    }

    void stop() {
        serviceResolver.close()
        dependencyWatcher.unregisterDependencies()
        serviceDiscovery.close()
        curatorFramework.close()
    }
}
