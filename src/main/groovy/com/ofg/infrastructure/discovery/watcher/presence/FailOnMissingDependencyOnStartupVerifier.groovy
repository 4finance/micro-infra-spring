package com.ofg.infrastructure.discovery.watcher.presence
import com.ofg.infrastructure.discovery.watcher.presence.checker.FailOnMissingDependencyChecker
import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker
import groovy.transform.TypeChecked

@TypeChecked
class FailOnMissingDependencyOnStartupVerifier extends DependencyPresenceOnStartupVerifier {

    private static final PresenceChecker DEFAULT_PRESENCE_CHECKER = new FailOnMissingDependencyChecker()
    
    FailOnMissingDependencyOnStartupVerifier() {
        super([:].withDefault { DEFAULT_PRESENCE_CHECKER })
    }

    FailOnMissingDependencyOnStartupVerifier(Map<String, PresenceChecker> presenceCheckers) {
        super(presenceCheckers.withDefault { DEFAULT_PRESENCE_CHECKER })
    }

}
