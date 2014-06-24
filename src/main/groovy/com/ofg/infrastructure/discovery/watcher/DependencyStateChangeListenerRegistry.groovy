package com.ofg.infrastructure.discovery.watcher

import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.state.ConnectionState
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.details.ServiceCacheListener

@TypeChecked
@PackageScope
class DependencyStateChangeListenerRegistry implements ServiceCacheListener {

    private final List<DependencyWatcherListener> listeners
    private final String dependencyName
    private final ServiceCache serviceCache

    DependencyStateChangeListenerRegistry(List<DependencyWatcherListener> listeners, String dependencyName, ServiceCache serviceCache) {
        this.listeners = listeners
        this.dependencyName = dependencyName
        this.serviceCache = serviceCache
    }

    @Override
    void cacheChanged() {
        listeners.each {
            it.stateChanged(dependencyName, serviceCache.instances.empty ? DependencyState.DISCONNECTED : DependencyState.CONNECTED)
        }
    }

    @Override
    void stateChanged(CuratorFramework client, ConnectionState newState) {
        // todo do something or ignore for what is worth
    }
}
