package com.ofg.infrastructure.discovery.util

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.ZookeeperServiceResolver
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.DependencyWatcherListener
import com.ofg.infrastructure.discovery.watcher.presence.FailOnMissingDependencyOnStartupVerifier
import groovy.transform.TypeChecked
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

@TypeChecked
class MicroDepsService {
    private  static final RetryNTimes DEFAULT_RETRY_POLICY = new RetryNTimes(20, 5000)

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
                .build()
        serviceDiscovery = ServiceDiscoveryBuilder.builder(Void).basePath(configurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build()
        dependencyWatcher = new DependencyWatcher(configurationResolver.dependencies, serviceDiscovery, new FailOnMissingDependencyOnStartupVerifier())
        serviceResolver = new ZookeeperServiceResolver(configurationResolver, serviceDiscovery)
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
