package com.ofg.infrastructure.metrics.config

import groovy.transform.CompileStatic
import org.apache.commons.lang.BooleanUtils
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

@CompileStatic
class IsGraphitePublishingEnabled implements Condition {

    public static final String GRAPHITE_PUBLISHING = 'graphite.publishing.enabled'

    @Override
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return graphitePublishingSwitchNotDefined(context) || graphitePublishingIsEnabled(context)
    }

    private boolean graphitePublishingSwitchNotDefined(ConditionContext context) {
        !context.environment.containsProperty(GRAPHITE_PUBLISHING)
    }

    private boolean graphitePublishingIsEnabled(ConditionContext context) {
        String graphiteEnabledValue = context.environment.getProperty(GRAPHITE_PUBLISHING).toLowerCase()
        return BooleanUtils.toBoolean(graphiteEnabledValue)
    }

}
