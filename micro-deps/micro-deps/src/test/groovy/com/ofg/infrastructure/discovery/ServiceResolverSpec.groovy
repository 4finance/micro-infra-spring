package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.base.SpecWithZookeper
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.checker.NoInstancesRunningException

import static org.codehaus.groovy.runtime.StackTraceUtils.extractRootCause
import static MicroserviceConfigurationUtil.REQUIRED_DEPENDENCY

class ServiceResolverSpec extends SpecWithZookeper {

    @Override
    String serviceConfig() {
        return REQUIRED_DEPENDENCY
    }

    def 'should throw exception if obligatory dependencies are missing'() {
        given:            
            DependencyWatcher dependencyWatcher = new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, 
                    new DefaultDependencyPresenceOnStartupVerifier() )
        when:
            dependencyWatcher.registerDependencies()    
        then:
            Throwable thrown = thrown(Throwable)
            extractRootCause(thrown).class == NoInstancesRunningException
    }

}
