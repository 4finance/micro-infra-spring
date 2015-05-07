package com.ofg.infrastructure.discovery.util;

import com.google.common.base.Predicate;

import java.util.Arrays;

public enum LoadBalancerType {
    STICKY, RANDOM, ROUND_ROBIN;

    public static LoadBalancerType fromName(final String strategyName) {
        LoadBalancerType loadBalancerType = CollectionUtils.find(Arrays.asList(values()), new Predicate<LoadBalancerType>() {
            @Override
            public boolean apply(LoadBalancerType input) {
                return input.name().equals(strategyName);
            }

        });
        if (loadBalancerType == null) {
            return ROUND_ROBIN;
        }
        return loadBalancerType;
    }

}
