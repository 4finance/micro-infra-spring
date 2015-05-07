package com.ofg.infrastructure.discovery.watcher.presence;

import com.ofg.infrastructure.discovery.watcher.presence.checker.FailOnMissingDependencyChecker;
import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker;
import org.apache.curator.x.discovery.ServiceCache;

@SuppressWarnings("unchecked")
public abstract class DependencyPresenceOnStartupVerifier {
    private static final PresenceChecker MANDATORY_DEPENDENCY_CHECKER = new FailOnMissingDependencyChecker();
    private final PresenceChecker optionalDependencyChecker;

    public DependencyPresenceOnStartupVerifier(PresenceChecker optionalDependencyChecker) {
        this.optionalDependencyChecker = optionalDependencyChecker;
    }

    public void verifyDependencyPresence(String dependencyName, ServiceCache serviceCache, boolean required) {
        if (required) {
            MANDATORY_DEPENDENCY_CHECKER.checkPresence(dependencyName, serviceCache.getInstances());
        } else {
            optionalDependencyChecker.checkPresence(dependencyName, serviceCache.getInstances());
        }
    }
}
