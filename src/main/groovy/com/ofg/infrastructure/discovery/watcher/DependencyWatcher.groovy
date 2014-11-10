package com.ofg.infrastructure.discovery.watcher

import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.ServiceDiscovery

@CompileStatic
class DependencyWatcher {

    private final Map<String, Map<String, String>> dependencies
    private final ServiceDiscovery serviceDiscovery
    private final Map<String, ServiceCache> dependencyRegistry = [:]
    private final List<DependencyWatcherListener> listeners = []
    private final DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier

    DependencyWatcher(Map<String, Map<String, String>> dependencies, ServiceDiscovery serviceDiscovery, DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier) {
        this.dependencies = dependencies
        this.serviceDiscovery = serviceDiscovery
        this.dependencyPresenceOnStartupVerifier = dependencyPresenceOnStartupVerifier
    }

    @PackageScope void registerDependencies() {
        dependencies.each { String dependencyName, Map<String, String> dependencyDefinition ->
            String path = dependencyDefinition['path']
            boolean required = dependencyDefinition['required']
            ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(path).build()
            serviceCache.start()
            dependencyPresenceOnStartupVerifier.verifyDependencyPresence(dependencyName, serviceCache, required)
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
