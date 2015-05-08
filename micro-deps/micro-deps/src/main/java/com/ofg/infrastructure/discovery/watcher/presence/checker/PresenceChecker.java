package com.ofg.infrastructure.discovery.watcher.presence.checker;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;

public interface PresenceChecker {

    /**
     * Checks if a given dependency is present
     *
     * @param dependencyName
     * @param serviceInstances - instances to check the dependency for
     */
    void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances);
}
