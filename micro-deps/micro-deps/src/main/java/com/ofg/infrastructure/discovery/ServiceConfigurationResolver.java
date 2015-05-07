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
    private final ServiceConfigurationValidator serviceConfigurationValidator;
    private final JsonToMicroserviceConfigurationConverter jsonToMicroserviceConfigurationConverter;

    public ServiceConfigurationResolver(String configuration) throws InvalidMicroserviceConfigurationException {
        this.serviceConfigurationValidator = new ServiceConfigurationValidator();
        this.jsonToMicroserviceConfigurationConverter = new JsonToMicroserviceConfigurationConverter();
        JSONObject parsedJson = serializeJson(configuration);
        String basePath = extractBasePath(parsedJson);
        JSONObject metaData = getJsonAsJsonObjectForBasePath(parsedJson, basePath);
        validateConfiguration(metaData);
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(metaData);
        ServicePath servicePath = retrieveThisElement(metaData);
        List<MicroserviceConfiguration.Dependency> dependencies = jsonToMicroserviceConfigurationConverter.convertJsonToDependencies(dependenciesAsJson);
        MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(servicePath, dependencies);
        this.basePath = basePath;
        this.microserviceConfiguration = microserviceConfiguration;
    }

    private ServicePath retrieveThisElement(JSONObject metaData) {
        return new ServicePath(metaData.getString(ServiceConfigurationProperties.THIS));
    }

    private JSONObject getJsonAsJsonObjectForBasePath(JSONObject parsedJson, String basePath) {
        return parsedJson.getJSONObject(basePath);
    }

    private JSONObject serializeJson(String configuration) {
        return (JSONObject) JSONSerializer.toJSON(StringUtils.deleteWhitespace(configuration));
    }

    private String extractBasePath(JSONObject parsedJson) {
        Set<String> rootKeys = serviceConfigurationValidator.checkThatJsonHasOneRootElement(parsedJson);
        return (String) rootKeys.toArray()[0];
    }

    private void validateConfiguration(JSONObject metaData) {
        serviceConfigurationValidator.checkThatServiceMetadataContainsValidElements(metaData);
        jsonToMicroserviceConfigurationConverter.convertFlatDependenciesToMapFormat(metaData);
        serviceConfigurationValidator.validateDependencyEntries(metaData);
        jsonToMicroserviceConfigurationConverter.setDefaultsForMissingOptionalElements(metaData);
    }

    private static JSONObject getDependenciesAsJsonObject(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get(ServiceConfigurationProperties.DEPENDENCIES);
        JSONObject dependenciesAsJson = (JSONObject) dependencies;
        return dependenciesAsJson;
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
