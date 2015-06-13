package com.ofg.infrastructure.metrics.publishing;

import com.google.common.base.Joiner;

/**
 * Builds metrics base path starting with root name followed by environment, country, application and node.
 * All mentioned path components are concatenated with periods and dots in node are replaced with underscores
 */
public class EnvironmentAwareMetricsBasePath implements MetricsBasePath {

    private final String path;

    public EnvironmentAwareMetricsBasePath(String rootName, String environment, String country, String application, String node) {
        path = Joiner.on(".").join(rootName, environment, country, application, replaceDots(node));
    }

    @Override
    public String getPath() {
        return path;
    }

    private String replaceDots(String name) {
        return name.replace(".", "_");
    }

    @Override
    public String toString() {
        return path;
    }
}
