package com.ofg.infrastructure.metrics.registry

import spock.lang.Specification
import spock.lang.Unroll

class MetricPathProviderSpec extends Specification {

    private static final ROOT = 'apps'
    private static final ENV = 'test'
    private static final COUNTRY = 'pl'
    private static final SERVICE = 'bluecash-adapter'
    private static final NODE = 'apl-001'
    private static final METRIC_PATH_PREFIX = "$ROOT.$ENV.$COUNTRY.$SERVICE.$NODE"

    MetricPathProvider metricPathProvider = new MetricPathProvider(ROOT, ENV, COUNTRY, SERVICE, NODE)
    
    @Unroll
    def 'should verify that name [#name] has path prepended [#alreadyPrepended]'() {
        expect:
            alreadyPrepended == metricPathProvider.isPathPrepended(metricName)
        where:
            metricName                          || alreadyPrepended
            'some_metric'                       || false
            "${METRIC_PATH_PREFIX}.some_metric" || true
    }
    
    @Unroll
    def 'should return metric path [#metricPath] for metric name [#metricName]'() {
        expect:
            metricPath == metricPathProvider.getMetricPath(metricName)
        where:
            metricName                          || metricPath
            'some_metric'                       || "${METRIC_PATH_PREFIX}.some_metric"
            "${METRIC_PATH_PREFIX}.some_metric" || "${METRIC_PATH_PREFIX}.some_metric"
    }

    def 'should replace dots in node name'() {
        given:
            MetricPathProvider pathProvider = new MetricPathProvider(ROOT, ENV, COUNTRY, SERVICE, nodeName)
        expect:
            pathProvider.getMetricPath('some_metric') == "$ROOT.$ENV.$COUNTRY.$SERVICE.${nodeNameWithReplacedDots}.some_metric"
        where:
            nodeName          || nodeNameWithReplacedDots
            'apl-001.ofg.com' || 'apl-001_ofg_com'
            'apl-001'         || 'apl-001'
    }
}
