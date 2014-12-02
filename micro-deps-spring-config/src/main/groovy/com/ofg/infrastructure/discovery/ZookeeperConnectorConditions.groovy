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
 * When {@link BasicProfiles#PRODUCTION} profile is active, standalone connector will be created.
 * When {@link BasicProfiles#DEVELOPMENT} or {@link BasicProfiles#TEST} is active, in-memory connector will be created.
 *
 * <p>Behavior can be overridden via system property <code>microservice.production</code>. When this property is present,
 * a production connector will be created despite the active Spring profile.
 *
 */
@CompileStatic
class ZookeeperConnectorConditions {

    static class StandaloneZookeeperCondition implements Condition {

        @Override
        boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            def activeProfiles = context.environment.activeProfiles
            PRODUCTION in activeProfiles || standaloneEnabled(context)
        }

    }

    static class InMemoryZookeeperCondition implements Condition {

        @Override
        boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            def activeProfiles = context.environment.activeProfiles
            (DEVELOPMENT in activeProfiles || TEST in activeProfiles) && !standaloneEnabled(context)
        }

    }

    private static boolean standaloneEnabled(ConditionContext context) {
        context.environment.containsProperty("zookeeper.standalone.enabled")
    }
}