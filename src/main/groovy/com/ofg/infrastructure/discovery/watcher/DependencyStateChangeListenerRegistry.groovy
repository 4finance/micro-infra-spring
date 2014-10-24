package com.ofg.infrastructure.discovery.watcher

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.state.ConnectionState
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.details.ServiceCacheListener

import static com.ofg.infrastructure.discovery.watcher.DependencyState.*

@Slf4j
@PackageScope
@CompileStatic
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
        DependencyState state = serviceCache.instances.empty ? DISCONNECTED : CONNECTED
        logCurrentState(state)
        listeners.each {
            it.stateChanged(dependencyName, state)
        }
    }

    private void logCurrentState(DependencyState dependencyState) {
        log.info("State of '${dependencyName}' service changed to ${dependencyState}.")
    }

    @Override
    void stateChanged(CuratorFramework client, ConnectionState newState) {
        // todo do something or ignore for what is worth
    }
}
