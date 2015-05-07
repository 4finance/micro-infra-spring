package com.ofg.infrastructure.discovery.util;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.curator.x.discovery.strategies.StickyStrategy;

import java.util.Map;

public class ProviderStrategyFactory {
    private static final Map<LoadBalancerType, StrategyFunction> STRATEGY_CREATORS =
            ImmutableMap.<LoadBalancerType, StrategyFunction>builder()
                    .put(LoadBalancerType.STICKY, new StrategyFunction(new StickyStrategy<>(new RoundRobinStrategy())))
                    .put(LoadBalancerType.RANDOM, new StrategyFunction(new RandomStrategy<>()))
                    .put(LoadBalancerType.ROUND_ROBIN, new StrategyFunction(new RoundRobinStrategy<>()))
                    .build();


    public ProviderStrategy createProviderStrategy(LoadBalancerType type) {
        return STRATEGY_CREATORS.get(type).apply(null);
    }

    static class StrategyFunction implements Function<Object, ProviderStrategy> {

        private final ProviderStrategy providerStrategy;

        public StrategyFunction(ProviderStrategy providerStrategy) {
            this.providerStrategy = providerStrategy;
        }

        @Override
        public ProviderStrategy apply(Object input) {
            return providerStrategy;
        }
    }
}

