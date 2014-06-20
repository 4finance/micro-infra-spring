package com.ofg.infrastructure.discovery
import groovy.transform.PackageScope
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.state.ConnectionState
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.details.ServiceCacheListener

class DependencyWatcher {
    
    private final Map<String, String> dependencies
    private final ServiceDiscovery serviceDiscovery
    private final Map<String, ServiceCache> dependencyRegistry = [:] 
    private final List<DependencyWatcherListener> listeners = []

    DependencyWatcher(Map<String, String> dependencies, ServiceDiscovery serviceDiscovery) {
        this.dependencies = dependencies
        this.serviceDiscovery = serviceDiscovery
    }
    
    @PackageScope void registerDependencies() {
       dependencies.each { String dependencyName, String dependencyDefinition ->
           ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(dependencyDefinition).build()
           serviceCache.start()
           dependencyRegistry[dependencyName] = serviceCache
           
           serviceCache.addListener(new ServiceCacheListener() {
               @Override
               void cacheChanged() {
                   listeners.each {
                       it.stateChanged(dependencyName, serviceCache.instances.empty ? com.ofg.infrastructure.discovery.DependencyState.DISCONNECTED : com.ofg.infrastructure.discovery.DependencyState.CONNECTED)
                   }
               }

               @Override
               void stateChanged(CuratorFramework client, ConnectionState newState) {
                    // todo do something or ignore for what is worth
               }
           })
       } 
    }
    
    @PackageScope void unregisterDependencies() {
        listeners.clear()
        dependencyRegistry.values().each {
            it.close()
        }
    }        
    
    void registerListener(DependencyWatcherListener listener) {
        listeners.add(listener)
    }
    
    void unregisterListener(DependencyWatcherListener listener) {
        listeners.remove(listener)
    }
}
