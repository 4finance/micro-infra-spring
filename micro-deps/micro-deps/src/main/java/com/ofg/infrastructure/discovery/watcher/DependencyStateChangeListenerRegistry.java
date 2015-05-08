package com.ofg.infrastructure.discovery.watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.ofg.infrastructure.discovery.watcher.DependencyState.CONNECTED;
import static com.ofg.infrastructure.discovery.watcher.DependencyState.DISCONNECTED;

public class DependencyStateChangeListenerRegistry implements ServiceCacheListener {

    private static final Logger log = LoggerFactory.getLogger(DependencyStateChangeListenerRegistry.class);

    private final List<DependencyWatcherListener> listeners;
    private final String dependencyName;
    private final ServiceCache serviceCache;

    public DependencyStateChangeListenerRegistry(List<DependencyWatcherListener> listeners, String dependencyName, ServiceCache serviceCache) {
        this.listeners = listeners;
        this.dependencyName = dependencyName;
        this.serviceCache = serviceCache;
    }

    @Override
    public void cacheChanged() {
        DependencyState state = serviceCache.getInstances().isEmpty() ? DISCONNECTED : CONNECTED;
        logCurrentState(state);
        informListeners(state);
    }

    private void logCurrentState(DependencyState dependencyState) {
        log.info("Service cache state change for '{}' instances, current service state: {}", dependencyName, dependencyState);
    }

    private void informListeners(DependencyState state) {
        for (DependencyWatcherListener listener : listeners) {
            listener.stateChanged(dependencyName, state);
        }
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        // todo do something or ignore for what is worth
    }
}
