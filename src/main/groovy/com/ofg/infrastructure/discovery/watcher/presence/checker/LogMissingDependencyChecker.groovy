package com.ofg.infrastructure.discovery.watcher.presence.checker

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.curator.x.discovery.ServiceInstance

@CompileStatic
@Slf4j
class LogMissingDependencyChecker implements PresenceChecker {
    @Override
    void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
        if (serviceInstances.empty) {
            log.warn("Microservice dependency with name [$dependencyName] is missing.")
        }
    }
}
