package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Alias to service as used e.g. in <code>ServiceRestClient</code>
 * You can translate from alias to path using {@link ServiceResolver#resolveAlias(com.ofg.infrastructure.discovery.ServiceAlias)}.
 */
@Immutable
@CompileStatic
class ServiceAlias {
    String name
}