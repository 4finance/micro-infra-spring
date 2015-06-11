package com.ofg.infrastructure.discovery.util;

import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.curator.x.discovery.strategies.StickyStrategy;

public class ProviderStrategyFactory {

    public ProviderStrategy createProviderStrategy(LoadBalancerType type) {
        switch (type) {
            case ROUND_ROBIN:
                return new RoundRobinStrategy<>();
            case RANDOM:
                return new RandomStrategy<>();
            case STICKY:
                return new StickyStrategy<>(new RoundRobinStrategy<>());
            default:
                throw new IllegalArgumentException("Unknown load balancer type " + type);
        }
    }
}

