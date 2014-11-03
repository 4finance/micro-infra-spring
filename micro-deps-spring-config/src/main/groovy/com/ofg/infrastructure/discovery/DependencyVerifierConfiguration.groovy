package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.watcher.presence.DependencyPresenceOnStartupVerifier
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@CompileStatic
class DependencyVerifierConfiguration {

    @Bean
    DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier() {
        return new DependencyPresenceOnStartupVerifier()
    }
}
