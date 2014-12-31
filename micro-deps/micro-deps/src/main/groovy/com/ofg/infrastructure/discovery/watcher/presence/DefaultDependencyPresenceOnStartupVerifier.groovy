package com.ofg.infrastructure.discovery.watcher.presence

import com.ofg.infrastructure.discovery.watcher.presence.checker.LogMissingDependencyChecker
import groovy.transform.CompileStatic

@CompileStatic
class DefaultDependencyPresenceOnStartupVerifier extends DependencyPresenceOnStartupVerifier {

    DefaultDependencyPresenceOnStartupVerifier() {
        super(new LogMissingDependencyChecker())
    }

}
