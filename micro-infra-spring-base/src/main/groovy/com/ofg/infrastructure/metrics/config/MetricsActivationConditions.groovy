package com.ofg.infrastructure.metrics.config

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotatedTypeMetadata

import static com.ofg.config.BasicProfiles.PRODUCTION

@CompileStatic
class MetricsActivationConditions {


    static class GraphiteEnabledCondition implements Condition {

        @Override
        boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment env = context.getEnvironment()
            return PRODUCTION in env.getActiveProfiles() || env.containsProperty("graphite.enabled")
        }
    }

}
