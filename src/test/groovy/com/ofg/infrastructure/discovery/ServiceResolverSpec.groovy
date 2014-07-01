package com.ofg.infrastructure.discovery
import com.ofg.infrastructure.base.SpecWithZookeper
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.presence.FailOnMissingDependencyOnStartupVerifier
import com.ofg.infrastructure.discovery.watcher.presence.checker.NoInstancesRunningException

import static org.codehaus.groovy.runtime.StackTraceUtils.extractRootCause

class ServiceResolverSpec extends SpecWithZookeper {
    
    def 'should throw exception if obligatory dependencies are missing'() {
        given:            
            DependencyWatcher dependencyWatcher = new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, 
                    new FailOnMissingDependencyOnStartupVerifier() )
        when:
            dependencyWatcher.registerDependencies()    
        then:
            Throwable thrown = thrown(Throwable)
            extractRootCause(thrown).class == NoInstancesRunningException
    }
    
}
