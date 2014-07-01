package com.ofg.infrastructure.discovery.watcher.presence.checker

import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceInstance

@TypeChecked
interface PresenceChecker {
    void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances)
}