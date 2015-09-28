package com.ofg.infrastructure.discovery.util;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Collections2;
import com.ofg.infrastructure.discovery.MicroserviceConfiguration;
import com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration;
import com.ofg.infrastructure.discovery.ServiceAlias;
import com.ofg.infrastructure.discovery.ServicePath;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DependencyCreator {

    public static Collection<MicroserviceConfiguration.Dependency> fromMap(Map<String, Map<String, Object>> dependency) {
        return Collections2.transform(dependency.entrySet(), new Function<Map.Entry<String, Map<String, Object>>, MicroserviceConfiguration.Dependency>() {
            @Override
            public MicroserviceConfiguration.Dependency apply(Map.Entry<String, Map<String, Object>> input) {
                String key = input.getKey();
                Map<String, Object> value = input.getValue();
                return new MicroserviceConfiguration.Dependency(new ServiceAlias(key),
                        new ServicePath((String) value.get("path")),
                        Boolean.valueOf((String) value.get("required")),
                        LoadBalancerType.fromName((String) value.get("load-balancer-type")),
                        MoreObjects.firstNonNull((String) value.get("contentTypeTemplate"), StringUtils.EMPTY),
                        MoreObjects.firstNonNull((String) value.get("version"), StringUtils.EMPTY),
                        MoreObjects.firstNonNull((Map<String, String>) value.get("headers"), new HashMap<String, String>()),
                        parseStubConfiguration(value));
            }

        });
    }

    private static StubsConfiguration parseStubConfiguration(Map<String, Object> value) {
        Map<String, String> stubs = (Map<String, String>) value.get("stubs");
        if (stubs != null) {
			return new StubsConfiguration(stubs.get("stubsGroupId"), stubs.get("stubsArtifactId"), stubs.get("stubsClassifier"));
		}
        return null;
    }

}
