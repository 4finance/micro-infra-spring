package com.ofg.infrastructure.discovery.watcher

import groovy.transform.PackageScope
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.ServiceDiscovery

class DependencyWatcher {

    private final Map<String, String> dependencies
    private final ServiceDiscovery serviceDiscovery
    private final Map<String, ServiceCache> dependencyRegistry = [:]
    private final List<DependencyWatcherListener> listeners = []
    private final Map<String, PresenceChecker> dependencyPresenceOnStartupChecker

    DependencyWatcher(Map<String, String> dependencies, ServiceDiscovery serviceDiscovery, Map<String, PresenceChecker> dependencyPresenceOnStartupChecker) {
        this.dependencies = dependencies
        this.serviceDiscovery = serviceDiscovery
        this.dependencyPresenceOnStartupChecker = dependencyPresenceOnStartupChecker
    }

    @PackageScope void registerDependencies() {
        dependencies.each { String dependencyName, String dependencyDefinition ->
            ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(dependencyDefinition).build()
            serviceCache.start()
            checkDependencyPresenceOnStartup(dependencyName, serviceCache)
            dependencyRegistry[dependencyName] = serviceCache
            serviceCache.addListener(new DependencyStateChangeListenerRegistry(listeners, dependencyName, serviceCache))
        }
    }

    private void checkDependencyPresenceOnStartup(String dependencyName, ServiceCache serviceCache) {
        PresenceChecker dependencyPresenceListener = dependencyPresenceOnStartupChecker[dependencyName]
        if (dependencyPresenceListener) {
            dependencyPresenceListener.checkPresence(serviceCache.instances)
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
