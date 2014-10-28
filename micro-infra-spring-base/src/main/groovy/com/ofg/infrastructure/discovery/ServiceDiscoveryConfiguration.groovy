package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

import static com.ofg.config.BasicProfiles.PRODUCTION

/**
 * This configuration imports configurations related to service discovery
 *
 * @see ServiceResolverConfiguration
 */
@CompileStatic
@Configuration
@Import(ServiceResolverConfiguration)
class ServiceDiscoveryConfiguration {

}
