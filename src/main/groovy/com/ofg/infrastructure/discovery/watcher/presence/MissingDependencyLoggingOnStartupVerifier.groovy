package com.ofg.infrastructure.discovery.watcher.presence

import com.ofg.infrastructure.discovery.watcher.presence.checker.LogMissingDependencyChecker
import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker
import groovy.transform.TypeChecked

@TypeChecked
class MissingDependencyLoggingOnStartupVerifier extends DependencyPresenceOnStartupVerifier {

    private static final PresenceChecker DEFAULT_PRESENCE_CHECKER = new LogMissingDependencyChecker()
    
    MissingDependencyLoggingOnStartupVerifier() {
        super([:].withDefault { DEFAULT_PRESENCE_CHECKER })
    }

    MissingDependencyLoggingOnStartupVerifier(Map<String, PresenceChecker> presenceCheckers) {
        super(presenceCheckers.withDefault { DEFAULT_PRESENCE_CHECKER })
    }

}
