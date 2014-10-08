package com.ofg.infrastructure.metrics.registry

import spock.lang.Specification
import spock.lang.Unroll

class MetricPathProviderSpec extends Specification {

    private static final ROOT_NAME = 'apps'
    private static final ENV = 'test'
    private static final COUNTRY = 'pl'
    private static final SERVICE_NAME = 'bluecash-adapter'
    private static final METRIC_PATH_PREFIX = "${ROOT_NAME}.${ENV}.${COUNTRY}.${SERVICE_NAME}"

    MetricPathProvider metricPathProvider = new MetricPathProvider(ROOT_NAME, ENV, COUNTRY, SERVICE_NAME)
    
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

}
