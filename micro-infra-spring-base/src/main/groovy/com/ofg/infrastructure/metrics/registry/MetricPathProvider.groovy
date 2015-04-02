package com.ofg.infrastructure.metrics.registry

import com.google.common.base.Joiner
import groovy.transform.CompileStatic

/**
 * This class is responsible for building your metric full path.
 *
 * It makes sure that metric names comply with the following template:
 *
 * <pre>
 *     (root-name).(environment).(country).(application-name).(node).(metric-name)
 * </pre>
 *
 * for example:
 *
 * <pre>
 *     apps.test.pl.bluecash-adapter.apl-001_4financegroup_com.transfer.request.balance.count
 * </pre>
 *
 * Also please note that dots in node names are replaced with underscores
 *
 */
@CompileStatic
class MetricPathProvider {
    private final String metricPathPrefix

    MetricPathProvider(String rootName, String environment, String country, String appName, String node) {
        metricPathPrefix = Joiner.on('.').join(rootName, environment, country, appName, replaceDots(node))
    }

    String getMetricPath(String metricName) {
        return isPathPrepended(metricName) ? metricName : Joiner.on('.').join(metricPathPrefix, metricName)
    }

    boolean isPathPrepended(String metricName) {
        return metricName.startsWith(metricPathPrefix)
    }

    private String replaceDots(String name) {
        return name.replace('.', '_')
    }
}
