package com.ofg.infrastructure.discovery.watcher.presence;

import com.ofg.infrastructure.discovery.watcher.presence.checker.LogMissingDependencyChecker;

public class DefaultDependencyPresenceOnStartupVerifier extends DependencyPresenceOnStartupVerifier {
    public DefaultDependencyPresenceOnStartupVerifier() {
        super(new LogMissingDependencyChecker());
    }
}
