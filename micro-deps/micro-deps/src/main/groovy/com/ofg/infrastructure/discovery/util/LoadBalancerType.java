package com.ofg.infrastructure.discovery.util;

import com.google.common.base.Predicate;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

public enum LoadBalancerType {
    STICKY, RANDOM, ROUND_ROBIN;

    public static LoadBalancerType fromName(final String strategyName) {
        LoadBalancerType loadBalancerType = CollectionUtils.find(Arrays.asList(values()), new Predicate<LoadBalancerType>() {
            @Override
            public boolean apply(LoadBalancerType input) {
                return input.name().equals(defaultIfEmpty(strategyName, EMPTY).toUpperCase());
            }

        });
        if (loadBalancerType == null) {
            return ROUND_ROBIN;
        }
        return loadBalancerType;
    }

}
