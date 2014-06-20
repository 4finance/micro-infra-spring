package com.ofg.infrastructure.discovery

interface DependencyWatcherListener {

    void stateChanged(String dependencyName, DependencyState newState)

}