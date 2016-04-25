package com.ofg.infrastructure.environment

import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll

class EnvironmentSetupVerifierSpec extends Specification {

    private static final List<String> POSSIBLE_PROFILES = ['dev', 'prod', 'test']
    private static final String UNKNOWN_PROFILE = 'unknownProfile'

    @Rule private final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private EnvironmentSetupVerifier environmentSetupVerifier = new EnvironmentSetupVerifier(POSSIBLE_PROFILES)

    private ConfigurableEnvironment environment = new MockEnvironment()
    private ApplicationEnvironmentPreparedEvent event = new ApplicationEnvironmentPreparedEvent(Stub(SpringApplication), [] as String[], environment)

    def "should not let run program without any given profile"() {
        given:
            exit.expectSystemExitWithStatus(1);
        when:
            environmentSetupVerifier.onApplicationEvent(event)
        then:
            thrown(CheckExitCalled)
    }

    def "should not let run program with unknown profile [#activeProfiles]"() {
        given:
            environment.setActiveProfiles(activeProfiles as String[])
            exit.expectSystemExitWithStatus(1);
        when:
            environmentSetupVerifier.onApplicationEvent(event)
        then:
            thrown(CheckExitCalled)
        where:
            activeProfiles << [ [UNKNOWN_PROFILE], [POSSIBLE_PROFILES.first(), UNKNOWN_PROFILE] ]
    }

}
