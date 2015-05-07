package com.ofg.infrastructure.discovery.util

import com.google.common.base.Function
import com.google.common.base.MoreObjects
import com.google.common.collect.Collections2
import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath
import org.apache.commons.lang.StringUtils

class DependencyCreator {

    static Collection<MicroserviceConfiguration.Dependency> fromMap(Map<String, Map<String, Object>> dependency) {
        return Collections2.transform(dependency.entrySet(),
                    new Function<Map.Entry<String, Map<String, Object>>, MicroserviceConfiguration.Dependency>() {
                        @Override
                        MicroserviceConfiguration.Dependency apply(Map.Entry<String, Map<String, Object>> input) {
                            String key = input.key
                            Map<String, Object> value = input.value
                            return new MicroserviceConfiguration.Dependency(new ServiceAlias(key),
                                    new ServicePath((String)value['path']),
                                    Boolean.valueOf((String)value['required']),
                                    LoadBalancerType.fromName((String)value['load-balancer-type']),
                                    MoreObjects.firstNonNull((String)value['contentTypeTemplate'], StringUtils.EMPTY),
                                    MoreObjects.firstNonNull((String)value['version'], StringUtils.EMPTY),
                                    MoreObjects.firstNonNull((Map<String, String>)value['headers'], new HashMap<String, String>()))
                        }
                    }
                )
    }
}
