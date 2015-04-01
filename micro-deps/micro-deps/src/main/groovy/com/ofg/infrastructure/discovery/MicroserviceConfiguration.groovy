package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.util.LoadBalancerType
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.ToString
import org.apache.commons.lang.StringUtils

@CompileStatic
@Immutable
@ToString
class MicroserviceConfiguration {
    ServicePath servicePath
    List<Dependency> dependencies = []

    @ToString
    @EqualsAndHashCode
    static class Dependency {
        final ServiceAlias serviceAlias
        final ServicePath servicePath
        final boolean required
        final LoadBalancerType loadBalancerType
        final String contentTypeTemplate
        final String version
        final Map<String, String> headers

        Dependency(ServiceAlias serviceAlias,
                   ServicePath servicePath,
                   boolean required = false,
                   LoadBalancerType loadBalancerType = LoadBalancerType.ROUND_ROBIN,
                   String contentTypeTemplate = StringUtils.EMPTY,
                   String version = StringUtils.EMPTY,
                   Map<String, String> headers = [:]) {
            this.serviceAlias = serviceAlias
            this.servicePath = servicePath
            this.required = required
            this.loadBalancerType = loadBalancerType
            this.contentTypeTemplate = contentTypeTemplate
            this.version = version
            this.headers = headers
        }
    }

    Dependency getDependencyForName(String serviceName) {
        return dependencies.find { it.serviceAlias.name == serviceName }
    }
}
