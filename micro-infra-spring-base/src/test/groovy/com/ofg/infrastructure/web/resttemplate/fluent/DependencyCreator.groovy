package com.ofg.infrastructure.web.resttemplate.fluent
import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.util.LoadBalancerType
import groovy.transform.CompileStatic

@CompileStatic
class DependencyCreator {

    static List<MicroserviceConfiguration.Dependency> fromMap(Map<String, Map<String, String>> dependency) {
        return dependency.collect { String key, Map<String, String> value ->
            new MicroserviceConfiguration.Dependency(new ServiceAlias(key),
                                                     new ServicePath(value['path']),
                                                     value['required'] as Boolean,
                                                     value['load-balancer-type'] as LoadBalancerType,
                                                     value['contentTypeTemplate'],
                                                     value['version'],
                                                     value['headers'] as Map<String, String>)
        }
    }
}
