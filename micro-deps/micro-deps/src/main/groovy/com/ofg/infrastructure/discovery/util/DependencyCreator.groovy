package com.ofg.infrastructure.discovery.util

import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath

class DependencyCreator {

    static List<MicroserviceConfiguration.Dependency> fromMap(Map<String, Map<String, String>> dependency) {
        return dependency.collect { String key, Map<String, String> value ->
            new MicroserviceConfiguration.Dependency(new ServiceAlias(key),
                    new ServicePath(value['path']),
                    value['required'] as Boolean ?: false,
                    value['load-balancer-type'] as LoadBalancerType ?: LoadBalancerType.ROUND_ROBIN,
                    value['contentTypeTemplate'] ?: '',
                    value['version'] ?: '',
                    value['headers'] as Map<String, String> ?: [:])
        }
    }
}
