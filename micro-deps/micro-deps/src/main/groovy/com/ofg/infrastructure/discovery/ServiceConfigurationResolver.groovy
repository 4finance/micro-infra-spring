package com.ofg.infrastructure.discovery

import com.google.common.base.Function
import com.google.common.base.Predicate
import com.google.common.collect.Collections2
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.ofg.infrastructure.discovery.util.CollectionUtils
import com.ofg.infrastructure.discovery.util.LoadBalancerType
import net.sf.json.JSON
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import org.apache.commons.lang.StringUtils

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH

class ServiceConfigurationResolver {

    final String basePath
    final MicroserviceConfiguration microserviceConfiguration

    ServiceConfigurationResolver(String configuration) throws InvalidMicroserviceConfigurationException {
        JSONObject parsedJson = (JSONObject) JSONSerializer.toJSON(StringUtils.deleteWhitespace(configuration));
        Set<String> rootKeys = checkThatJsonHasOneRootElement(parsedJson);
        String basePath = rootKeys.toArray()[0];
        JSONObject metaData = parsedJson.getJSONObject(basePath);
        validateConfiguration(metaData)
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(metaData)
        ServicePath servicePath = new ServicePath(metaData.getString("this"))
        List<MicroserviceConfiguration.Dependency> dependencies = convertJsonToDependencies(dependenciesAsJson)
        MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(servicePath, dependencies)
        this.basePath = basePath
        this.microserviceConfiguration = microserviceConfiguration
    }

    private List<MicroserviceConfiguration.Dependency> convertJsonToDependencies(JSONObject dependenciesAsJson) {
        return new ArrayList<>(Collections2.transform(dependenciesAsJson.entrySet(), new Function<Map.Entry<String, JSONObject>, MicroserviceConfiguration.Dependency>() {
            @Override
            MicroserviceConfiguration.Dependency apply(Map.Entry<String, JSONObject> input) {
                String alias = input.key
                String path = input.value.getString("path")
                boolean required = getPropertyOrDefault(input.value, "required", Boolean.FALSE)
                String loadBalancerName = getPropertyOrDefault(input.value, "load-balancer", "")
                LoadBalancerType loadBalancerType = LoadBalancerType.fromName(loadBalancerName)
                String contentTypeTemplate = getPropertyOrDefault(input.value, "contentTypeTemplate", StringUtils.EMPTY)
                String version = getPropertyOrDefault(input.value, "version", StringUtils.EMPTY)
                JSONObject headers = input.value.get("headers")
                Map<String, String> headersAsMap = Maps.newHashMap()
                if (headers != null) {
                    for (Map.Entry<String, JSONObject> entry : headers.entrySet()) {
                        headersAsMap.put(entry.key, entry.value)
                    }
                }
                return new MicroserviceConfiguration.Dependency(new ServiceAlias(alias), new ServicePath(path), required, loadBalancerType, contentTypeTemplate, version, headersAsMap)
            }
        }))
    }

    private void validateConfiguration(JSONObject metaData) {
        checkThatServiceMetadataContainsValidElements(metaData)
        convertFlatDependenciesToMapFormat(metaData)
        validateDependencyEntries(metaData)
        setDefaultsForMissingOptionalElements(metaData)
    }

    private static <T> T getPropertyOrDefault(JSONObject jsonObject, String propertyName, T defaultValue) {
        return jsonObject.has(propertyName) ? (T) jsonObject.get(propertyName) : defaultValue;
    }

    private static Set<String> checkThatJsonHasOneRootElement(JSONObject parsedJson) {
        Set<String> rootKeys = parsedJson.keySet()
        if (rootKeys.size() != 1) {
            throw new InvalidMicroserviceConfigurationException('multiple root elements')
        }
        return rootKeys
    }

    private static void checkThatServiceMetadataContainsValidElements(JSONObject serviceMetadata) {
        Object thisValue = serviceMetadata.get("this")
        if (!(thisValue && thisValue instanceof String)) {
            throw new InvalidMicroserviceConfigurationException('invalid or missing "this" element')
        }
        Object dependencies = serviceMetadata.get("dependencies")
        if (dependencies != null && (dependencies instanceof JSONObject)) {
            JSONObject dependenciesAsJson = (JSONObject) dependencies
            for (Object dependency : dependenciesAsJson.values()) {
                if (!dependency instanceof Map) {
                    throw new InvalidMicroserviceConfigurationException('invalid "dependencies" element')
                }
            }
        }
        if (dependencies != null && !(dependencies instanceof JSONObject)) {
            throw new InvalidMicroserviceConfigurationException('invalid "dependencies" element')
        }
    }

    private static void convertFlatDependenciesToMapFormat(JSONObject serviceMetadata) {
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(serviceMetadata)
        if (dependenciesAsJson == null) {
            return
        }
        for (Map.Entry<String, Object> dependency : dependenciesAsJson.entrySet()) {
            if (dependency.value instanceof String) {
                dependenciesAsJson.put(dependency.key, ImmutableMap.of(PATH, dependency.value))
            }
        }
    }

    private static JSONObject getDependenciesAsJsonObject(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get("dependencies")
        JSONObject dependenciesAsJson = (JSONObject) dependencies
        return dependenciesAsJson
    }

    private static void validateDependencyEntries(JSONObject serviceMetadata) {
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(serviceMetadata)
        List<String> invalidDependenciesNames = Lists.newArrayList()
        if (dependenciesAsJson == null) {
            return
        }
        for (Map.Entry<String, Object> dependency : dependenciesAsJson.entrySet()) {
            if (!dependency.value instanceof Map) {
                invalidDependenciesNames.add(dependency.key)
            }
        }
        if (!invalidDependenciesNames.isEmpty()) {
            throw new InvalidMicroserviceConfigurationException("following dependencies have invalid format: " +
                    " $invalidDependenciesNames (Check documentation for details.)")
        }
    }

    private static void setDefaultsForMissingOptionalElements(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get("dependencies")
        if (dependencies == null) {
            serviceMetadata.put("dependencies", new JSONObject())
        }
    }

    String getMicroserviceName() {
        return microserviceConfiguration.servicePath.path
    }

    List<MicroserviceConfiguration.Dependency> getDependencies() {
        return microserviceConfiguration.dependencies
    }

    MicroserviceConfiguration.Dependency getDependencyForName(String serviceName) {
        return CollectionUtils.find(microserviceConfiguration.dependencies, new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.serviceAlias.name == serviceName
            }
        })
    }

    LoadBalancerType getLoadBalancerTypeOf(ServicePath dependencyPath) {
        MicroserviceConfiguration.Dependency dependency = CollectionUtils.find(microserviceConfiguration.dependencies, new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.servicePath == dependencyPath
            }
        })
        return dependency == null ? LoadBalancerType.ROUND_ROBIN : dependency.loadBalancerType
    }

}
