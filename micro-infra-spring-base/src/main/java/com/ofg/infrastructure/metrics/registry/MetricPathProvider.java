package com.ofg.infrastructure.metrics.registry;

import com.google.common.base.Joiner;

/**
 * This class is responsible for building your metric full path.
 * <p/>
 * It makes sure that metric names comply with the following template:
 * <p/>
 * <pre>
 *     (root-name).(environment).(country).(application-name).(node).(metric-name)
 * </pre>
 * <p/>
 * for example:
 * <p/>
 * <pre>
 *     apps.test.pl.bluecash-adapter.apl-001_4financegroup_com.transfer.request.balance.count
 * </pre>
 * <p/>
 * Also please note that dots in node names are replaced with underscores
 */
public class MetricPathProvider {

    private final String metricPathPrefix;

    public MetricPathProvider(String rootName, String environment, String country, String appName, String node) {
        metricPathPrefix = Joiner.on(".").join(rootName, environment, country, appName, replaceDots(node));
    }

    public String getMetricPath(String metricName) {
        return isPathPrepended(metricName) ? metricName : Joiner.on(".").join(metricPathPrefix, metricName);
    }

    public boolean isPathPrepended(String metricName) {
        return metricName.startsWith(metricPathPrefix);
    }

    private String replaceDots(String name) {
        return name.replace(".", "_");
    }
}
