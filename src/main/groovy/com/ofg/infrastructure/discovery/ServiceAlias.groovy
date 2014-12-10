package com.ofg.infrastructure.discovery

import groovy.transform.Immutable

/**
 * Alias to service as used e.g. in <code>ServiceRestClient</code>
 * You can translate from alias to path using {@link ServiceResolver#resolveAlias(com.ofg.infrastructure.discovery.ServiceAlias)}.
 */
@Immutable
class ServiceAlias {
    String name
}

/**
 * Path to dependency as registered in service resolver, like ZooKeeper
 */
@Immutable
class ServicePath {
    String path
}
