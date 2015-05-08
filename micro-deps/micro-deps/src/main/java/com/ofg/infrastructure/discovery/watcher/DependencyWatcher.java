package com.ofg.infrastructure.discovery.watcher;

import com.ofg.infrastructure.discovery.MicroserviceConfiguration;
import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyWatcher {

    private final List<MicroserviceConfiguration.Dependency> dependencies;
    private final ServiceDiscovery serviceDiscovery;
    private final Map<String, ServiceCache> dependencyRegistry = new HashMap<String, ServiceCache>();
    private final List<DependencyWatcherListener> listeners = new ArrayList<DependencyWatcherListener>();
    private final DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier;

    public DependencyWatcher(List<MicroserviceConfiguration.Dependency> dependencies, ServiceDiscovery serviceDiscovery, DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier) {
        this.dependencies = dependencies;
        this.serviceDiscovery = serviceDiscovery;
        this.dependencyPresenceOnStartupVerifier = dependencyPresenceOnStartupVerifier;
    }

    public void registerDependencies() throws Exception {
        for (MicroserviceConfiguration.Dependency dependency : dependencies) {
            String dependencyName = dependency.getServiceAlias().getName();
            String path = dependency.getServicePath().getPath();
            boolean required = dependency.isRequired();
            ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(path).build();
            serviceCache.start();
            dependencyPresenceOnStartupVerifier.verifyDependencyPresence(dependencyName, serviceCache, required);
            dependencyRegistry.put(dependencyName, serviceCache);
            serviceCache.addListener(new DependencyStateChangeListenerRegistry(listeners, dependencyName, serviceCache));
        }
    }

    public void unregisterDependencies() throws IOException {
        listeners.clear();
        for (ServiceCache cache : dependencyRegistry.values()) {
            cache.close();
        }
    }

    public void registerDependencyStateChangeListener(DependencyWatcherListener listener) {
        listeners.add(listener);
    }

    public void unregisterDependencyStateChangeListener(DependencyWatcherListener listener) {
        listeners.remove(listener);
    }

}
