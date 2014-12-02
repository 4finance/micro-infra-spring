package com.ofg.infrastructure.discovery

import com.ofg.config.BasicProfiles
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotatedTypeMetadata

import static com.ofg.config.BasicProfiles.PRODUCTION

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
            Environment env = context.environment
            PRODUCTION in env.activeProfiles || env.containsProperty("zookeeper.standalone.enabled")
        }

    }

    static class InMemoryZookeeperCondition implements Condition {

        @Override
        boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            !new StandaloneZookeeperCondition().matches(context, metadata)
        }

    }

}