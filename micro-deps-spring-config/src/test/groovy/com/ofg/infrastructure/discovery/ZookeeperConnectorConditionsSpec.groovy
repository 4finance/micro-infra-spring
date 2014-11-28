package com.ofg.infrastructure.discovery

import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.config.BasicProfiles.*

class ZookeeperConnectorConditionsSpec extends Specification {

    static final testingCondition = new ZookeeperConnectorConditions.TestingZookeeperCondition()
    static final productionCondition = new ZookeeperConnectorConditions.ProductionZookeeperCondition()
    static final forceProd = "microservice.production"

    ConditionContext context = Mock()
    AnnotatedTypeMetadata metadata = Mock()

    @Unroll
    def '[#condition.class.simpleName] should evaluate to [#expectedOutcome] with profile [#activeProfile] and env[#envProperty]'() {
        given:
            def mockEnv = new MockEnvironment()
                    .withProperty(envProperty, "")
            mockEnv.addActiveProfile(activeProfile)
            context.environment >> mockEnv
        when:
            boolean matches = condition.matches(context, metadata)
        then:
            matches == expectedOutcome
        where:
            condition           | activeProfile | envProperty || expectedOutcome
            productionCondition | PRODUCTION    | "foo"       || true
            productionCondition | PRODUCTION    | forceProd   || true
            productionCondition | DEVELOPMENT   | "foo"       || false
            productionCondition | DEVELOPMENT   | forceProd   || true
            productionCondition | TEST          | "foo"       || false
            productionCondition | TEST          | forceProd   || true
            testingCondition    | DEVELOPMENT   | "foo"       || true
            testingCondition    | DEVELOPMENT   | forceProd   || false
            testingCondition    | TEST          | "foo"       || true
            testingCondition    | TEST          | forceProd   || false
            testingCondition    | PRODUCTION    | "foo"       || false
            testingCondition    | PRODUCTION    | forceProd   || false
    }
}
