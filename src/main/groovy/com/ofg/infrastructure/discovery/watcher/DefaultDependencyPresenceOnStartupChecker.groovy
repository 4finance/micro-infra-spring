package com.ofg.infrastructure.discovery.watcher

import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceInstance

@TypeChecked
class DefaultDependencyPresenceOnStartupChecker implements PresenceChecker {
    @Override
    void checkPresence(List<ServiceInstance> serviceInstances) {
        if (serviceInstances.empty) {
            throw new NoInstancesRunningException()
        }
    }
}
