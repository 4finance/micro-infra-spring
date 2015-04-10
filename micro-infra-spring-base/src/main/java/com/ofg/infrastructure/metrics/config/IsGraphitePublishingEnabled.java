package com.ofg.infrastructure.metrics.config;

import com.ofg.infrastructure.config.PropertyAbsentOrEnabledCondition;


public class IsGraphitePublishingEnabled extends PropertyAbsentOrEnabledCondition {

    public static final String GRAPHITE_PUBLISHING = "graphite.publishing.enabled";

    public IsGraphitePublishingEnabled() {
        super(GRAPHITE_PUBLISHING);
    }
}
