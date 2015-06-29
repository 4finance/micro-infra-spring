package com.ofg.infrastructure.metrics.publishing

import spock.lang.Specification

class EnvironmentAwareMetricsBasePathSpec extends Specification {
    private static final ROOT = 'apps'
    private static final ENV = 'test'
    private static final COUNTRY = 'pl'
    private static final SERVICE = 'bluecash-adapter'

    def 'should construct metrics base path'() {
        given:
            EnvironmentAwareMetricsBasePath basePath = new EnvironmentAwareMetricsBasePath(ROOT, ENV, COUNTRY, SERVICE, 'node1')
        expect:
            basePath.path == "$ROOT.$ENV.$COUNTRY.${SERVICE}.node1"
    }

    def 'should replace dots in node name'() {
        given:
            EnvironmentAwareMetricsBasePath basePath = new EnvironmentAwareMetricsBasePath(ROOT, ENV, COUNTRY, SERVICE, nodeName)
        expect:
            basePath.path == "$ROOT.$ENV.$COUNTRY.${SERVICE}.$sanitizedNodeName"
        where:
            nodeName          | sanitizedNodeName
            'apl-001'         | 'apl-001'
            'apl-001.ofg.com' | 'apl-001_ofg_com'
            '192.168.1.5'     | '192_168_1_5'
    }
}
