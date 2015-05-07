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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH;

public class ServiceConfigurationResolver {

    private final String basePath;
    private final MicroserviceConfiguration microserviceConfiguration;

    public ServiceConfigurationResolver(String configuration) throws InvalidMicroserviceConfigurationException {
        JSONObject parsedJson = (JSONObject) JSONSerializer.toJSON(StringUtils.deleteWhitespace(configuration));
        Set<String> rootKeys = checkThatJsonHasOneRootElement(parsedJson);
        String basePath = (String) rootKeys.toArray()[0];
        JSONObject metaData = parsedJson.getJSONObject(basePath);
        validateConfiguration(metaData);
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(metaData);
        ServicePath servicePath = new ServicePath(metaData.getString("this"));
        List<MicroserviceConfiguration.Dependency> dependencies = convertJsonToDependencies(dependenciesAsJson);
        MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(servicePath, dependencies);
        this.basePath = basePath;
        this.microserviceConfiguration = microserviceConfiguration;
    }

    private List<MicroserviceConfiguration.Dependency> convertJsonToDependencies(JSONObject dependenciesAsJson) {
        return new ArrayList(Collections2.transform(dependenciesAsJson.entrySet(), new Function<Map.Entry<String, JSONObject>, MicroserviceConfiguration.Dependency>() {
            @Override
            public MicroserviceConfiguration.Dependency apply(Map.Entry<String, JSONObject> input) {
                String alias = input.getKey();
                String path = input.getValue().getString("path");
                boolean required = getPropertyOrDefault(input.getValue(), "required", Boolean.FALSE);
                String loadBalancerName = getPropertyOrDefault(input.getValue(), "load-balancer", "");
                LoadBalancerType loadBalancerType = LoadBalancerType.fromName(loadBalancerName);
                String contentTypeTemplate = getPropertyOrDefault(input.getValue(), "contentTypeTemplate", StringUtils.EMPTY);
                String version = getPropertyOrDefault(input.getValue(), "version", StringUtils.EMPTY);
                JSONObject headers = (JSONObject) input.getValue().get("headers");
                Map<String, String> headersAsMap = Maps.newHashMap();
                if (headers != null) {
                    for (Object entry : headers.entrySet()) {
                        Map.Entry<String, Object> headerEntry = (Map.Entry<String, Object>) entry;
                        headersAsMap.put(headerEntry.getKey(), String.valueOf(headerEntry.getValue()));
                    }
                }
                return new MicroserviceConfiguration.Dependency(new ServiceAlias(alias), new ServicePath(path), required, loadBalancerType, contentTypeTemplate, version, headersAsMap);
            }
        }));
    }

    private void validateConfiguration(JSONObject metaData) {
        checkThatServiceMetadataContainsValidElements(metaData);
        convertFlatDependenciesToMapFormat(metaData);
        validateDependencyEntries(metaData);
        setDefaultsForMissingOptionalElements(metaData);
    }

    private static <T> T getPropertyOrDefault(JSONObject jsonObject, String propertyName, T defaultValue) {
        return jsonObject.has(propertyName) ? (T) jsonObject.get(propertyName) : defaultValue;
    }

    private static Set<String> checkThatJsonHasOneRootElement(JSONObject parsedJson) {
        Set<String> rootKeys = parsedJson.keySet();
        if (rootKeys.size() != 1) {
            throw new InvalidMicroserviceConfigurationException("multiple root elements");
        }

        return rootKeys;
    }

    private static void checkThatServiceMetadataContainsValidElements(JSONObject serviceMetadata) {
        Object thisValue = serviceMetadata.get("this");
        if (!(thisValue != null && (thisValue instanceof String))) {
            throw new InvalidMicroserviceConfigurationException("invalid or missing \"this\" element");
        }
        Object dependencies = serviceMetadata.get("dependencies");
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

    private static void convertFlatDependenciesToMapFormat(JSONObject serviceMetadata) {
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(serviceMetadata);
        if (dependenciesAsJson == null) {
            return;
        }
        for (Object dependency : dependenciesAsJson.entrySet()) {
            Map.Entry<String, Object> entryDependency = (Map.Entry<String, Object>) dependency;
            if (entryDependency.getValue() instanceof String) {
                dependenciesAsJson.put(entryDependency.getKey(), ImmutableMap.of(PATH, entryDependency.getValue()));
            }
        }
    }

    private static JSONObject getDependenciesAsJsonObject(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get("dependencies");
        JSONObject dependenciesAsJson = (JSONObject) dependencies;
        return dependenciesAsJson;
    }

    private static void validateDependencyEntries(JSONObject serviceMetadata) {
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

    private static void setDefaultsForMissingOptionalElements(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get("dependencies");
        if (dependencies == null) {
            serviceMetadata.put("dependencies", new JSONObject());
        }

    }

    public String getMicroserviceName() {
        return microserviceConfiguration.getServicePath().getPath();
    }

    public List<MicroserviceConfiguration.Dependency> getDependencies() {
        return microserviceConfiguration.getDependencies();
    }

    public MicroserviceConfiguration.Dependency getDependencyForName(final String serviceName) {
        return CollectionUtils.find(microserviceConfiguration.getDependencies(), new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            public boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.getServiceAlias().getName().equals(serviceName);
            }

        });
    }

    public LoadBalancerType getLoadBalancerTypeOf(final ServicePath dependencyPath) {
        MicroserviceConfiguration.Dependency dependency = CollectionUtils.find(microserviceConfiguration.getDependencies(), new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            public boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.getServicePath().equals(dependencyPath);
            }

        });
        return dependency == null ? LoadBalancerType.ROUND_ROBIN : dependency.getLoadBalancerType();
    }

    public String getBasePath() {
        return basePath;
    }

    public MicroserviceConfiguration getMicroserviceConfiguration() {
        return microserviceConfiguration;
    }
}
