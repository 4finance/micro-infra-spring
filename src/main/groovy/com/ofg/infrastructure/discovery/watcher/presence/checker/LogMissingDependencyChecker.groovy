package com.ofg.infrastructure.discovery.watcher.presence.checker

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.apache.curator.x.discovery.ServiceInstance

@TypeChecked
@Slf4j
class LogMissingDependencyChecker implements PresenceChecker {
    @Override
    void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
        if (serviceInstances.empty) {
            log.warn("Microservice dependency with name [$dependencyName] is missing.")
        }
    }
}
