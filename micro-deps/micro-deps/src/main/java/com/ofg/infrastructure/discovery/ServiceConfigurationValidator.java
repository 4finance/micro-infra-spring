package com.ofg.infrastructure.discovery;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ofg.infrastructure.discovery.util.CollectionUtils;
import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH;

class ServiceConfigurationValidator {

    Set<String> checkThatJsonHasOneRootElement(JSONObject parsedJson) {
        Set<String> rootKeys = parsedJson.keySet();
        if (rootKeys.size() != 1) {
            throw new InvalidMicroserviceConfigurationException("multiple root elements");
        }
        return rootKeys;
    }

    void checkThatServiceMetadataContainsValidElements(JSONObject serviceMetadata) {
        Object thisValue = serviceMetadata.get(ServiceConfigurationProperties.THIS);
        if (!(thisValue != null && (thisValue instanceof String))) {
            throw new InvalidMicroserviceConfigurationException("invalid or missing \"this\" element");
        }
        Object dependencies = serviceMetadata.get(ServiceConfigurationProperties.DEPENDENCIES);
        if (dependencies != null && (dependencies instanceof JSONObject)) {
            JSONObject dependenciesAsJson = (JSONObject) dependencies;
            for (Object dependency : dependenciesAsJson.values()) {
                if (dependency == null) {
                    throw new InvalidMicroserviceConfigurationException("invalid \"dependencies\" element - you're missing a value there");
                }
            }
        }
        if (dependencies != null && !(dependencies instanceof JSONObject)) {
            throw new InvalidMicroserviceConfigurationException("invalid \"dependencies\" element");
        }
    }

    JSONObject getDependenciesAsJsonObject(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get(ServiceConfigurationProperties.DEPENDENCIES);
        JSONObject dependenciesAsJson = (JSONObject) dependencies;
        return dependenciesAsJson;
    }

    void validateDependencyEntries(JSONObject serviceMetadata) {
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(serviceMetadata);
        List<String> invalidDependenciesNames = Lists.newArrayList();
        if (dependenciesAsJson == null) {
            return;

        }
        for (Object dependency : dependenciesAsJson.entrySet()) {
            Map.Entry<String, Object> entryDependency = (Map.Entry<String, Object>) dependency;
            if (entryDependency.getValue() != null && !(entryDependency.getValue() instanceof Map)) {
                invalidDependenciesNames.add(entryDependency.getKey());
            }
        }
        if (!invalidDependenciesNames.isEmpty()) {
            throw new InvalidMicroserviceConfigurationException("following dependencies have invalid format: " + " " + String.valueOf(invalidDependenciesNames) + " (Check documentation for details.)");
        }
    }

}
