package com.ofg.infrastructure.discovery.watcher

interface DependencyWatcherListener {

    void stateChanged(String dependencyName, DependencyState newState)

}