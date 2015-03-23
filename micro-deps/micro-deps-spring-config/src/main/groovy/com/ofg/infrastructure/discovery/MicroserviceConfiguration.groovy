package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.util.LoadBalancerType
import groovy.transform.CompileStatic
import groovy.transform.Immutable

@CompileStatic
@Immutable
class MicroserviceConfiguration {
    String microserviceName


    @Immutable
    static class Dependency {
        ServiceAlias serviceAlias
        ServicePath servicePath
        boolean required
        LoadBalancerType loadBalancerType
        String contentTypeTemplate
        String version
    }
}
