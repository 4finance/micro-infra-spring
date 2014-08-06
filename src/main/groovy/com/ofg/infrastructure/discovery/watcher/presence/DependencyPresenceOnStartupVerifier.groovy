package com.ofg.infrastructure.discovery.watcher.presence

import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker
import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceCache

/**
 * Verifies if a dependency is present and passes execution to 
 * proper {@see PresenceChecker}. Used by {@see DependencyWatcher}
 */
@TypeChecked
abstract class DependencyPresenceOnStartupVerifier {
    private final Map<String, PresenceChecker> presenceCheckers

    DependencyPresenceOnStartupVerifier(Map<String, PresenceChecker> presenceCheckers) {
        this.presenceCheckers = presenceCheckers
    }

    void verifyDependencyPresence(String dependencyName, ServiceCache serviceCache) {
        PresenceChecker dependencyPresenceChecker = presenceCheckers[dependencyName]
        if (dependencyPresenceChecker) {
            dependencyPresenceChecker.checkPresence(dependencyName, serviceCache.instances)
        }
    }
}
