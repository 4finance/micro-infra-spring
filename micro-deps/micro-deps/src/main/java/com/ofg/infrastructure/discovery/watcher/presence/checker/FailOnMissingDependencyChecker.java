package com.ofg.infrastructure.discovery.watcher.presence.checker;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;

public class FailOnMissingDependencyChecker implements PresenceChecker {
    @Override
    public void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
        if (serviceInstances.isEmpty()) {
            throw new NoInstancesRunningException(dependencyName);
        }
    }

}
