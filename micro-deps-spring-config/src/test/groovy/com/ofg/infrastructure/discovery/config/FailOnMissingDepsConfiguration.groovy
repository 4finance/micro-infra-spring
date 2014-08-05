package com.ofg.infrastructure.discovery.config

import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.FailOnMissingDependencyOnStartupVerifier
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@TypeChecked
@Configuration
class FailOnMissingDepsConfiguration {

    @Bean
    DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier() {
        return new FailOnMissingDependencyOnStartupVerifier()
    }
}
