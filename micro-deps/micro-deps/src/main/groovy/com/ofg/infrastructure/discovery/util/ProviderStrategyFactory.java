package com.ofg.infrastructure.discovery.util;

import com.google.common.collect.ImmutableMap;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.curator.x.discovery.strategies.StickyStrategy;

import java.util.Map;

public class ProviderStrategyFactory {

    private static final Map<LoadBalancerType, ProviderStrategy> STRATEGY_CREATORS =
            ImmutableMap.<LoadBalancerType, ProviderStrategy>builder()
                    .put(LoadBalancerType.STICKY, new StickyStrategy<>(new RoundRobinStrategy()))
                    .put(LoadBalancerType.RANDOM, new RandomStrategy<>())
                    .put(LoadBalancerType.ROUND_ROBIN, new RoundRobinStrategy<>())
                    .build();


    public ProviderStrategy createProviderStrategy(LoadBalancerType type) {
        return STRATEGY_CREATORS.get(type);
    }

}

