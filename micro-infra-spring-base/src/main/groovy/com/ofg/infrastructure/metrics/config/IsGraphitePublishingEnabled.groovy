package com.ofg.infrastructure.metrics.config

import com.ofg.infrastructure.config.PropertyAbsentOrEnabledCondition
import groovy.transform.CompileStatic

@CompileStatic
class IsGraphitePublishingEnabled extends PropertyAbsentOrEnabledCondition {

    public static final String GRAPHITE_PUBLISHING = 'graphite.publishing.enabled'

    IsGraphitePublishingEnabled() {
        super(GRAPHITE_PUBLISHING)
    }

}
