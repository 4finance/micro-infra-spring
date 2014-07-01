package com.ofg.infrastructure.discovery.watcher

import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import groovy.transform.PackageScope
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.ServiceDiscovery

class DependencyWatcher {

    private final Map<String, String> dependencies
    private final ServiceDiscovery serviceDiscovery
    private final Map<String, ServiceCache> dependencyRegistry = [:]
    private final List<DependencyWatcherListener> listeners = []
    private final DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier

    DependencyWatcher(Map<String, String> dependencies, ServiceDiscovery serviceDiscovery, DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier) {
        this.dependencies = dependencies
        this.serviceDiscovery = serviceDiscovery
        this.dependencyPresenceOnStartupVerifier = dependencyPresenceOnStartupVerifier
    }

    @PackageScope void registerDependencies() {
        dependencies.each { String dependencyName, String dependencyDefinition ->
            ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(dependencyDefinition).build()
            serviceCache.start()
            dependencyPresenceOnStartupVerifier.verifyDependencyPresence(dependencyName, serviceCache)
            dependencyRegistry[dependencyName] = serviceCache
            serviceCache.addListener(new DependencyStateChangeListenerRegistry(listeners, dependencyName, serviceCache))
        }
    }

    @PackageScope void unregisterDependencies() {
        listeners.clear()
        dependencyRegistry.values().each {
            it.close()
        }
    }

    void registerDependencyStateChangeListener(DependencyWatcherListener listener) {
        listeners.add(listener)
    }

    void unregisterDependencyStateChangeListener(DependencyWatcherListener listener) {
        listeners.remove(listener)
    }
}
