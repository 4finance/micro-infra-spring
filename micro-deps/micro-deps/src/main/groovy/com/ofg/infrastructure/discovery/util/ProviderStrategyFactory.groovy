package com.ofg.infrastructure.discovery.util

import org.apache.curator.x.discovery.ProviderStrategy
import org.apache.curator.x.discovery.strategies.RandomStrategy
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy
import org.apache.curator.x.discovery.strategies.StickyStrategy

class ProviderStrategyFactory {

    private static final Map<LoadBalancerType, Closure> STRATEGY_CREATORS =
            [(LoadBalancerType.STICKY)         :   { return new StickyStrategy<>(new RoundRobinStrategy()) },
             (LoadBalancerType.RANDOM)         :   { return new RandomStrategy<>()},
             (LoadBalancerType.ROUND_ROBIN)    :   { return new RoundRobinStrategy<>() }] as EnumMap

    ProviderStrategy createProviderStrategy(LoadBalancerType type) {
        return STRATEGY_CREATORS[type]() as ProviderStrategy
    }
}

enum LoadBalancerType {
    STICKY, RANDOM, ROUND_ROBIN

    static LoadBalancerType fromName(String strategyName) {
        values().findResult(ROUND_ROBIN) { if (it.name() == strategyName) it }
    }
}

