package com.ofg.infrastructure.discovery;

import com.google.common.base.Predicate;
import com.ofg.infrastructure.discovery.util.CollectionUtils;
import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Deprecated but used to represent the descriptor for micro-infra-view
 */
@Deprecated
public class ServiceConfigurationResolver {

    private final String basePath;
    private final MicroserviceConfiguration microserviceConfiguration;
    private final ServiceConfigurationValidator serviceConfigurationValidator;
    private final JsonToMicroserviceConfigurationConverter jsonToMicroserviceConfigurationConverter;
    private final String configurationAsString;

    public ServiceConfigurationResolver(String configurationAsString) throws InvalidMicroserviceConfigurationException {
        this.serviceConfigurationValidator = new ServiceConfigurationValidator();
        this.jsonToMicroserviceConfigurationConverter = new JsonToMicroserviceConfigurationConverter();
        JSONObject parsedJson = serializeJson(configurationAsString);
        String basePath = extractBasePath(parsedJson);
        JSONObject metaData = getJsonAsJsonObjectForBasePath(parsedJson, basePath);
        validateConfiguration(metaData);
        JSONObject dependenciesAsJson = getDependenciesAsJsonObject(metaData);
        ServicePath servicePath = retrieveThisElement(metaData);
        List<MicroserviceConfiguration.Dependency> dependencies = jsonToMicroserviceConfigurationConverter.convertJsonToDependencies(dependenciesAsJson);
        jsonToMicroserviceConfigurationConverter.addAllDefaultJsonObjectValues(dependenciesAsJson);
        MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(servicePath, dependencies);
        this.basePath = basePath;
        this.microserviceConfiguration = microserviceConfiguration;
        this.configurationAsString = parsedJson.toString();
    }

    public ServiceConfigurationResolver(String basePath, MicroserviceConfiguration microserviceConfiguration) {
        this.basePath = basePath;
        this.microserviceConfiguration = microserviceConfiguration;
        this.serviceConfigurationValidator =  new ServiceConfigurationValidator();
        this.jsonToMicroserviceConfigurationConverter = new JsonToMicroserviceConfigurationConverter();
        this.configurationAsString = MicroserviceConfigurationToJsonConverter.fromConfiguration(basePath, microserviceConfiguration);
    }

    private ServicePath retrieveThisElement(JSONObject metaData) {
        return new ServicePath(metaData.getString(ServiceConfigurationProperties.THIS));
    }

    private JSONObject getJsonAsJsonObjectForBasePath(JSONObject parsedJson, String basePath) {
        return parsedJson.getJSONObject(basePath);
    }

    private JSONObject serializeJson(String configurationAsString) {
        return (JSONObject) JSONSerializer.toJSON(StringUtils.deleteWhitespace(configurationAsString));
    }

    private String extractBasePath(JSONObject parsedJson) {
        Set<String> rootKeys = serviceConfigurationValidator.checkThatJsonHasOneRootElement(parsedJson);
        return (String) rootKeys.toArray()[0];
    }

    private void validateConfiguration(JSONObject metaData) {
        serviceConfigurationValidator.checkThatServiceMetadataContainsValidElements(metaData);
        JsonToMicroserviceConfigurationConverter.convertFlatDependenciesToMapFormat(metaData);
        serviceConfigurationValidator.validateDependencyEntries(metaData);
        JsonToMicroserviceConfigurationConverter.setDefaultsForMissingOptionalElements(metaData);
    }

    private static JSONObject getDependenciesAsJsonObject(JSONObject serviceMetadata) {
        Object dependencies = serviceMetadata.get(ServiceConfigurationProperties.DEPENDENCIES);
        JSONObject dependenciesAsJson = (JSONObject) dependencies;
        return dependenciesAsJson;
    }

    /**
     *
     * @deprecated since 0.9.1, use {@link #getMicroservicePath()} instead
     */
    @Deprecated
    public String getMicroserviceName() {
        return microserviceConfiguration.getServicePath().getPath();
    }

    public ServicePath getMicroservicePath() {
        return microserviceConfiguration.getServicePath();
    }

    public List<MicroserviceConfiguration.Dependency> getDependencies() {
        return microserviceConfiguration.getDependencies();
    }

    public MicroserviceConfiguration.Dependency getDependency(final ServiceAlias serviceAlias) {
        return CollectionUtils.find(microserviceConfiguration.getDependencies(), new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            public boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.getServiceAlias().equals(serviceAlias);
            }
        });
    }

    /**
     *
     * @deprecated since 0.9.1, use {@link #getDependency(ServiceAlias serviceAlias)} instead
     */
    @Deprecated
    public MicroserviceConfiguration.Dependency getDependencyForName(final String serviceName) {
        return getDependency(new ServiceAlias(serviceName));
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

    public String getConfigurationAsString() {
        return configurationAsString;
    }
}
