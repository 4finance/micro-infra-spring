package com.ofg.infrastructure.discovery

import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.config.BasicProfiles.*

class ZookeeperConnectorConditionsSpec extends Specification {

    private static final IN_MEMORY_CONDITION = new ZookeeperConnectorConditions.InMemoryZookeeperCondition()
    private static final STANDALONE_CONDITION = new ZookeeperConnectorConditions.StandaloneZookeeperCondition()
    private static final STANDALONE = "zookeeper.standalone.enabled"

    ConditionContext context = Mock()
    AnnotatedTypeMetadata metadata = Mock()

    def '[#condition.class.simpleName] should evaluate to [#expectedOutcome] with profile [#activeProfile] and env[#envProperty]'() {
        given:
            MockEnvironment mockEnv = new MockEnvironment()
                    .withProperty(envProperty, "")
            mockEnv.addActiveProfile(activeProfile)
            context.environment >> mockEnv
        when:
            boolean matches = condition.matches(context, metadata)
        then:
            matches == expectedOutcome
        where:
            condition            | activeProfile | envProperty || expectedOutcome
            STANDALONE_CONDITION | PRODUCTION    | "foo"       || true
            STANDALONE_CONDITION | PRODUCTION    | STANDALONE  || true
            STANDALONE_CONDITION | DEVELOPMENT   | "foo"       || false
            STANDALONE_CONDITION | DEVELOPMENT   | STANDALONE  || true
            STANDALONE_CONDITION | TEST          | "foo"       || false
            STANDALONE_CONDITION | TEST          | STANDALONE  || true
            IN_MEMORY_CONDITION  | DEVELOPMENT   | "foo"       || true
            IN_MEMORY_CONDITION  | DEVELOPMENT   | STANDALONE  || false
            IN_MEMORY_CONDITION  | TEST          | "foo"       || true
            IN_MEMORY_CONDITION  | TEST          | STANDALONE  || false
            IN_MEMORY_CONDITION  | PRODUCTION    | "foo"       || false
            IN_MEMORY_CONDITION  | PRODUCTION    | STANDALONE  || false
    }
}
