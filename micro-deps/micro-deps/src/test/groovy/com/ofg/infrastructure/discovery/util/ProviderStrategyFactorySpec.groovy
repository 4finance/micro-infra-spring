package com.ofg.infrastructure.discovery.util

import org.apache.curator.x.discovery.strategies.RandomStrategy
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy
import org.apache.curator.x.discovery.strategies.StickyStrategy
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.infrastructure.discovery.util.LoadBalancerType.*

class ProviderStrategyFactorySpec extends Specification {

    def 'should create #strategyProviderName provider for #strategyName'() {
        given:
            ProviderStrategyFactory factory = new ProviderStrategyFactory()
        when:
            def strategyProvider = factory.createProviderStrategy(strategyName)
        then:
            strategyProvider.class == strategyProviderName
        where:
            strategyName  | strategyProviderName
            STICKY        | StickyStrategy
            RANDOM        | RandomStrategy
            ROUND_ROBIN   | RoundRobinStrategy
    }

}
