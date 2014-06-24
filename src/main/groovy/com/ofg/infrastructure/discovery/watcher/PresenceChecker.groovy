package com.ofg.infrastructure.discovery.watcher

import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceInstance

@TypeChecked
interface PresenceChecker {
    void checkPresence(List<ServiceInstance> serviceInstances)
}