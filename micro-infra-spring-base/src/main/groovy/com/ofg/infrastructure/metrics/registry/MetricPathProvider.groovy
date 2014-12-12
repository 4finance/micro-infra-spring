package com.ofg.infrastructure.metrics.registry

import com.google.common.base.Joiner
import groovy.transform.CompileStatic

/**
 * This class is responsible for preparing your metric full path. It also verifies whether your metric name is already  
 *
 * We have a following template for metrics:
 *
 * <pre>
 *     (root-name).(environment).(country).(application-name).(metric-name)
 * </pre>
 *
 * for example:
 *
 * <pre>
 *     apps.test.pl.bluecash-adapter.transfer.request.balance.count
 * </pre>
 *
 */
@CompileStatic
class MetricPathProvider {
    private final String metricPathPrefix

    MetricPathProvider(String rootName, String environment, String country, String appName) {
        metricPathPrefix = Joiner.on('.').join(rootName, environment, country, appName)
    }

    String getMetricPath(String metricName) {
        return isPathPrepended(metricName) ? metricName : Joiner.on('.').join(metricPathPrefix, metricName)
    }

    boolean isPathPrepended(String metricName) {
        return metricName.startsWith(metricPathPrefix)
    }
}
