package com.ofg.infrastructure.discovery

import com.ofg.config.BasicProfiles
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

import static com.ofg.config.BasicProfiles.*

/**
 * Conditions for creating a zookeeper connector.
 *
 * When {@link BasicProfiles#PRODUCTION} profile is active, production connector will be created.
 * When {@link BasicProfiles#DEVELOPMENT} or {@link BasicProfiles#TEST} is active, testing connector will be created.
 *
 * <p>Behavior can be overridden via system property <code>microservice.production</code>. When this property is present,
 * a production connector will be created despite the active Spring profile.
 *
 */
class ZookeeperConnectorConditions {

    @CompileStatic
    static class ProductionZookeeperCondition implements Condition {

        @Override
        boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            def activeProfiles = context.environment.activeProfiles
            PRODUCTION in activeProfiles || forceProduction(context)
        }

    }

    @CompileStatic
    static class TestingZookeeperCondition implements Condition {

        @Override
        boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            def activeProfiles = context.environment.activeProfiles
            (DEVELOPMENT in activeProfiles || TEST in activeProfiles) && !forceProduction(context)
        }

    }

    static boolean forceProduction(ConditionContext context) {
        context.environment.containsProperty("microservice.production")
    }
}