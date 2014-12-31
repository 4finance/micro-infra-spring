package com.ofg.infrastructure.discovery.watcher.presence.checker

import groovy.transform.CompileStatic
import org.apache.curator.x.discovery.ServiceInstance

@CompileStatic
class FailOnMissingDependencyChecker implements PresenceChecker {
    @Override
    void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
        if (serviceInstances.empty) {
            throw new NoInstancesRunningException(dependencyName)
        }
    }
}
