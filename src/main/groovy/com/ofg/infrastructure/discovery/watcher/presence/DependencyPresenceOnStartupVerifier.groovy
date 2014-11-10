package com.ofg.infrastructure.discovery.watcher.presence

import com.ofg.infrastructure.discovery.watcher.presence.checker.FailOnMissingDependencyChecker
import com.ofg.infrastructure.discovery.watcher.presence.checker.LogMissingDependencyChecker
import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker
import groovy.transform.CompileStatic
import org.apache.curator.x.discovery.ServiceCache

@CompileStatic
abstract class DependencyPresenceOnStartupVerifier {

    private static final PresenceChecker MANDATORY_DEPENDENCY_CHECKER = new FailOnMissingDependencyChecker()
    private final PresenceChecker optionalDependencyChecker

    DependencyPresenceOnStartupVerifier(PresenceChecker optionalDependencyChecker) {
        this.optionalDependencyChecker = optionalDependencyChecker
    }

    void verifyDependencyPresence(String dependencyName, ServiceCache serviceCache, boolean required) {
        if (required) {
            MANDATORY_DEPENDENCY_CHECKER.checkPresence(dependencyName, serviceCache.instances)
        } else {
            optionalDependencyChecker.checkPresence(dependencyName, serviceCache.instances)
        }
    }

}
