package com.ofg.infrastructure.discovery.watcher.presence.checker

import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceInstance

@TypeChecked
class FailOnMissingDependencyChecker implements PresenceChecker {
    @Override
    void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
        if (serviceInstances.empty) {
            throw new NoInstancesRunningException(dependencyName)
        }
    }
}
