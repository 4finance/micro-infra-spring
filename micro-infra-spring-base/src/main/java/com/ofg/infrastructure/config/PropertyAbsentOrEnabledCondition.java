package com.ofg.infrastructure.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class PropertyAbsentOrEnabledCondition implements Condition {

    private final String filteringProperty;

    public PropertyAbsentOrEnabledCondition(String filteringProperty) {
        this.filteringProperty = filteringProperty;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return switchNotDefined(context) || switchIsEnabled(context);
    }

    private boolean switchNotDefined(ConditionContext context) {
        return isBlank(getFilteringPropertyFrom(context));
    }

    private boolean switchIsEnabled(ConditionContext context) {
        String propertyEnabled = getFilteringPropertyFrom(context).toLowerCase();
        return toBoolean(propertyEnabled);
    }

    private String getFilteringPropertyFrom(ConditionContext context) {
        return context.getEnvironment().getProperty(filteringProperty);
    }
}
