package com.ofg.infrastructure.metrics.registry

import spock.lang.Specification
import spock.lang.Unroll

class MetricPathProviderSpec extends Specification {

    public static final ROOT_NAME = 'apps'
    public static final ENV = 'test' 
    public static final COUNTRY = 'pl' 
    public static final SERVICE_NAME = 'bluecash-adapter'

    MetricPathProvider metricPathProvider = new MetricPathProvider(ROOT_NAME, ENV, COUNTRY, SERVICE_NAME)
    
    @Unroll
    def 'should verify that name [#name] has path prepended [#alreadyPrepended]'() {
        expect:
            alreadyPrepended == metricPathProvider.isPathPrepended(metricName)
        where:
            metricName                                                   || alreadyPrepended
            'some_metric'                                                || false
            "${ROOT_NAME}.${ENV}.${COUNTRY}.${SERVICE_NAME}.some_metric" || true
            
    }
    
    @Unroll
    def 'should return metric path [#metricPath] for metric name [#metricName]'() {
        expect:
            metricPath == metricPathProvider.getMetricPath(metricName)
        where:
            metricName                                                   || metricPath
            'some_metric'                                                || "${ROOT_NAME}.${ENV}.${COUNTRY}.${SERVICE_NAME}.some_metric"
            "${ROOT_NAME}.${ENV}.${COUNTRY}.${SERVICE_NAME}.some_metric" || "${ROOT_NAME}.${ENV}.${COUNTRY}.${SERVICE_NAME}.some_metric"
    }

}
