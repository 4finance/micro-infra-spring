package com.ofg.infrastructure.discovery.watcher.presence

import com.ofg.infrastructure.discovery.watcher.presence.checker.FailOnMissingDependencyChecker
import com.ofg.infrastructure.discovery.watcher.presence.checker.LogMissingDependencyChecker
import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker
import groovy.transform.CompileStatic
import org.apache.curator.x.discovery.ServiceCache

@CompileStatic
class DependencyPresenceOnStartupVerifier {

    private static final PresenceChecker MANDATORY_DEPENDENCY_CHECKER = new FailOnMissingDependencyChecker()
    private static final PresenceChecker OPTIONAL_DEPENDENCY_CHECKER = new LogMissingDependencyChecker()

    void verifyDependencyPresence(String dependencyName, ServiceCache serviceCache, boolean required) {
        if (required) {
            MANDATORY_DEPENDENCY_CHECKER.checkPresence(dependencyName, serviceCache.instances)
        } else {
            OPTIONAL_DEPENDENCY_CHECKER.checkPresence(dependencyName, serviceCache.instances)
        }
    }

}
