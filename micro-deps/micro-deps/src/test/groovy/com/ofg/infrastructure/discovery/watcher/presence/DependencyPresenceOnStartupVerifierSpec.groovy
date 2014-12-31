package com.ofg.infrastructure.discovery.watcher.presence

import com.ofg.infrastructure.discovery.watcher.presence.checker.PresenceChecker
import org.apache.curator.x.discovery.ServiceCache
import org.apache.curator.x.discovery.ServiceInstance
import spock.lang.Specification

class DependencyPresenceOnStartupVerifierSpec extends Specification {

    private static final String SERVICE_NAME = 'service01'

    def 'should check optional dependency using optional dependency checker'() {
        given:
            PresenceChecker optionalDependencyChecker = Mock()
            DependencyPresenceOnStartupVerifier dependencyVerifier = new DependencyPresenceOnStartupVerifier(optionalDependencyChecker) {
            }
            ServiceCache serviceCache = Mock()
            serviceCache.instances >> new ArrayList<ServiceInstance>()
        when:
            dependencyVerifier.verifyDependencyPresence(SERVICE_NAME, serviceCache, false)
        then:
            1 * optionalDependencyChecker.checkPresence(SERVICE_NAME, serviceCache.instances)
    }

}
