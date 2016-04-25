package com.ofg.infrastructure.discovery.util

import spock.lang.Specification
import spock.lang.Unroll

class LoadBalancerTypeSpec extends Specification {

    def 'should return ROUND_ROBIN if invalid value [#value] has been provided'() {
        expect:
            LoadBalancerType.ROUND_ROBIN == LoadBalancerType.fromName(value)
        where:
            value << [null, 'NON_EXISTING_VALUE']
    }

    def 'should return proper enum if existing value of enum has been provided'() {
        expect:
            LoadBalancerType.STICKY == LoadBalancerType.fromName('sticky')
    }


}
