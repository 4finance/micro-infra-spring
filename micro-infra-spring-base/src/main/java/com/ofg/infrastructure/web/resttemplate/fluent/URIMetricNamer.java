package com.ofg.infrastructure.web.resttemplate.fluent;

import java.net.URI;

/**
 * A strategy for inventing (parts of) metric names for metrics relating to {@link java.net.URI}-s.
 * <p/>
 * The purpose of this interface is to allow the Users to elide any dynamic parts of the URI from the metric name
 * or - more generally - allow the Users to fine-tune the granularity of metric names used by the
 * {@link com.ofg.infrastructure.web.resttemplate.RestOperationsMetricsAspect}. Therefore, in most cases
 * implementations should elide any path- and uri- parameters found in the given URIs, as well as any other parts
 * that might lead to a situation where the set of possible names returned by the implementation in a given application
 * is unbounded.
 * <p/>
 * Returned metric names may only consist of: latin alphanumerics, dots, and underscores.
 * The returned names must not start with a dot.
 */
public interface URIMetricNamer {
    String metricNameFor(URI uri);
}
