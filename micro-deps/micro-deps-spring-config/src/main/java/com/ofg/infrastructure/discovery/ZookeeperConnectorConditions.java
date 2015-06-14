package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;

import static com.ofg.config.BasicProfiles.PRODUCTION;

/**
 * Conditions for creating a zookeeper connector.
 * <p/>
 * When {@link BasicProfiles#PRODUCTION} profile is active, standalone connector will be created.
 * When {@link BasicProfiles#DEVELOPMENT} or {@link BasicProfiles#TEST} is active, in-memory connector will be created.
 * <p/>
 * <p>Behavior can be overridden via system property {@code zookeeper.standalone.enabled}. When this property is present,
 * a standalone connector will be created despite the active Spring profile.
 */
public class ZookeeperConnectorConditions {
    public static class StandaloneZookeeperCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment env = context.getEnvironment();
            return Arrays.asList(env.getActiveProfiles()).contains(PRODUCTION)  || env.containsProperty("zookeeper.standalone.enabled");
        }
    }

    public static class InMemoryZookeeperCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !new StandaloneZookeeperCondition().matches(context, metadata);
        }

    }
}
