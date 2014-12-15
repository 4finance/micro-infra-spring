package com.ofg.infrastructure.discovery.util

import com.ofg.infrastructure.discovery.InstanceDetails
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

@TypeChecked
class MicroDepsService {
    private  static final RetryPolicy DEFAULT_RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500)

    private ServiceConfigurationResolver configurationResolver
    private DependencyWatcher dependencyWatcher
    private CuratorFramework curatorFramework
    private ServiceInstance serviceInstance
    private ServiceDiscovery serviceDiscovery
    private ServiceResolver serviceResolver

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
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails).basePath(configurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build()
        dependencyWatcher = new DependencyWatcher(configurationResolver.dependencies, serviceDiscovery, new DefaultDependencyPresenceOnStartupVerifier())
        serviceResolver = new ZookeeperServiceResolver(configurationResolver, serviceDiscovery)
    }

    private InstanceDetails instanceDetails() {
        List<String> dependenciesList = configurationResolver.dependencies.collect {
            return it.value[(PATH)] as String
        }
        return new InstanceDetails(dependenciesList)
    }


    void registerDependencyStateChangeListener(DependencyWatcherListener listener) {
        dependencyWatcher.registerDependencyStateChangeListener(listener)
    }

    void start() {
        curatorFramework.start()
        serviceDiscovery.start()
        dependencyWatcher.registerDependencies()
        serviceResolver.start()
    }

    void stop() {
        serviceResolver.close()
        dependencyWatcher.unregisterDependencies()
        serviceDiscovery.close()
        curatorFramework.close()
    }
}
