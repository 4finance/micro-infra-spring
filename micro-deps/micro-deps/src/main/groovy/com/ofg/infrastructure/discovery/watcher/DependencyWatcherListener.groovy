package com.ofg.infrastructure.discovery.watcher

/**
 * Performs logic upon change of state of a dependency {@see DependencyState} 
 * in the service discovery system.
 */
interface DependencyWatcherListener {

    /**
     * Method executed upon state change of a dependency
     * @param dependency - alias from microservice configuration {@see ServiceConfigurationResolver}
     * @param newState
     */
    void stateChanged(String dependencyName, DependencyState newState)

}