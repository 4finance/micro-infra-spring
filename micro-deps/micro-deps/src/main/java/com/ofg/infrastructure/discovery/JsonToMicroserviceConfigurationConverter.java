package com.ofg.infrastructure.discovery;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration;
import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH;

class JsonToMicroserviceConfigurationConverter {

    public static final String COLON_DEPENDENCY_SEPARATOR = ":";

    List<MicroserviceConfiguration.Dependency> convertJsonToDependencies(JSONObject dependenciesAsJson) {
        return new ArrayList(Collections2.transform(dependenciesAsJson.entrySet(), new Function<Map.Entry<String, JSONObject>, MicroserviceConfiguration.Dependency>() {
            @Override
            public MicroserviceConfiguration.Dependency apply(Map.Entry<String, JSONObject> input) {
                String alias = input.getKey();
                String path = input.getValue().getString(ServiceConfigurationProperties.PATH);
                boolean required = getPropertyOrDefault(input.getValue(), ServiceConfigurationProperties.REQUIRED, Boolean.FALSE);
                String loadBalancerName = getPropertyOrDefault(input.getValue(), ServiceConfigurationProperties.LOAD_BALANCER, "");
                LoadBalancerType loadBalancerType = LoadBalancerType.fromName(loadBalancerName);
                String contentTypeTemplate = getPropertyOrDefault(input.getValue(), ServiceConfigurationProperties.CONTENT_TYPE_TEMPLATE, StringUtils.EMPTY);
                String version = getPropertyOrDefault(input.getValue(), ServiceConfigurationProperties.VERSION, StringUtils.EMPTY);
                JSONObject headers = (JSONObject) input.getValue().get(ServiceConfigurationProperties.HEADERS);
                Map<String, String> headersAsMap = Maps.newHashMap();
                fillHeadersIfPresent(headers, headersAsMap);
                String stubs = (String) input.getValue().get(ServiceConfigurationProperties.STUBS);
                ServicePath servicePath = new ServicePath(path);
                StubsConfiguration stubsConfiguration = parseStubConfiguration(stubs, servicePath);
                return new MicroserviceConfiguration.Dependency(new ServiceAlias(alias), servicePath, required, loadBalancerType,
                        contentTypeTemplate, version, headersAsMap, stubsConfiguration);
            }
        }));
    }

    JSONObject addAllDefaultJsonObjectValues(JSONObject dependenciesAsJson) {
        Set<Map.Entry<String, JSONObject>> set = dependenciesAsJson.entrySet();
        for(Map.Entry<String, JSONObject> entry: set) {
            String stubs = (String) entry.getValue().get(ServiceConfigurationProperties.STUBS);
            if (StringUtils.isBlank(stubs)) {
                String path = entry.getValue().getString(ServiceConfigurationProperties.PATH);
                ServicePath servicePath = new ServicePath(path);
                StubsConfiguration stubsConfiguration = new StubsConfiguration(servicePath);
                entry.getValue().put(ServiceConfigurationProperties.STUBS, stubsConfiguration.toColonSeparatedDependencyNotation());
            }
        }
        return dependenciesAsJson;
    }

    private StubsConfiguration parseStubConfiguration(String stubs, ServicePath servicePath) {
        StubsConfiguration stubsConfiguration = new StubsConfiguration(servicePath);
        if (StringUtils.isNotBlank(stubs)) {
            String[] splitStubDependency = stubs.split(COLON_DEPENDENCY_SEPARATOR);
            if (splitStubDependency.length < 2) {
                throw new InvalidStubDefinitionException("Dependency [" + stubs + "] doesn't have a proper colon separated dependency notation. " +
                        "E.g. 'foo.bar:artifact-name:classifier' or 'foo.bar:artifact-name' for default 'stubs' classifier value");
            }
            String stubGroupId = splitStubDependency[0];
            String stubArtifactId = splitStubDependency[1];
            if (StringUtils.isNotBlank(stubGroupId) && StringUtils.isNotBlank(stubArtifactId)) {
                return new StubsConfiguration(stubGroupId, stubArtifactId, stubClassifierOrEmptyIfNotPresent(splitStubDependency));
            }
        }
        return stubsConfiguration;
    }

    private String stubClassifierOrEmptyIfNotPresent(String[] splitStubDependency) {
        if (splitStubDependency.length == 3) {
            return splitStubDependency[2];
        }
        return StringUtils.EMPTY;
    }

    private void fillHeadersIfPresent(JSONObject headers, Map<String, String> headersAsMap) {
        if (headers != null) {
            for (Object entry : headers.entrySet()) {
                Map.Entry<String, Object> headerEntry = (Map.Entry<String, Object>) entry;
                headersAsMap.put(headerEntry.getKey(), String.valueOf(headerEntry.getValue()));
            }
        }
    }

    private static <T> T getPropertyOrDefault(JSONObject jsonObject, String propertyName, T defaultValue) {
        return jsonObject.has(propertyName) ? (T) jsonObject.get(propertyName) : defaultValue;
    }

    static void convertFlatDependenciesToMapFormat(JSONObject serviceMetadata) {
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
        Object dependencies = serviceMetadata.get(ServiceConfigurationProperties.DEPENDENCIES);
        return (JSONObject) dependencies;
    }


    static void setDefaultsForMissingOptionalElements(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get(ServiceConfigurationProperties.DEPENDENCIES);
        if (dependencies == null) {
            serviceMetadata.put(ServiceConfigurationProperties.DEPENDENCIES, new JSONObject());
        }

    }

    public static class InvalidStubDefinitionException extends RuntimeException {
        public InvalidStubDefinitionException(String message) {
            super(message);
        }
    }

}
