package com.ofg.infrastructure.metrics.publishing;

/**
 * Provides metrics base path for publishing purposes.
 * MetricsBasePath is intended to be used when publishing metrics to remote servers to distinguish metrics published from multiple sources.
 */
public interface MetricsBasePath {

    String getPath();
}
