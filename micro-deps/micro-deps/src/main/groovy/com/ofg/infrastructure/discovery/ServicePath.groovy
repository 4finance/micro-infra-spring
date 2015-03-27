package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Path to dependency as registered in service resolver, like ZooKeeper
 */
@Immutable
@CompileStatic
class ServicePath {
    String path
}