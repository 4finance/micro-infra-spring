package com.ofg.infrastructure.discovery;

import net.sf.json.JSONObject;

public class MicroserviceConfigurationToJsonConverter {

    public static String fromConfiguration(String basePath, MicroserviceConfiguration microserviceConfiguration) {
        JSONObject microserviceJson = new JSONObject();
        microserviceJson.put(basePath, corePartFromConfig(microserviceConfiguration));
        return microserviceJson.toString();
    }

    private static JSONObject corePartFromConfig(MicroserviceConfiguration microserviceConfiguration) {
        JSONObject microserviceJsonCorePart = new JSONObject();
        microserviceJsonCorePart.put(ServiceConfigurationProperties.THIS, microserviceConfiguration.getServicePath().getPath());
        microserviceJsonCorePart.put(ServiceConfigurationProperties.DEPENDENCIES, dependenciesFromConfig(microserviceConfiguration));
        return microserviceJsonCorePart;
    }

    private static JSONObject dependenciesFromConfig(MicroserviceConfiguration microserviceConfiguration) {
        JSONObject dependencies = new JSONObject();
        for (MicroserviceConfiguration.Dependency microserviceDependency : microserviceConfiguration.getDependencies()) {
            dependencies.put(microserviceDependency.getServiceAlias().getName(), dependencyFromConfig(microserviceDependency));
        }
        return dependencies;
    }

    private static JSONObject dependencyFromConfig(MicroserviceConfiguration.Dependency microserviceDependency) {
        JSONObject dependency = new JSONObject();
        dependency.put(ServiceConfigurationProperties.PATH, microserviceDependency.getServicePath().getPath());
        dependency.put(ServiceConfigurationProperties.STUBS, microserviceDependency.getStubs().toColonSeparatedDependencyNotation());
        dependency.put(ServiceConfigurationProperties.LOAD_BALANCER, microserviceDependency.getLoadBalancerType());
        dependency.put(ServiceConfigurationProperties.REQUIRED, microserviceDependency.isRequired());
        dependency.put(ServiceConfigurationProperties.CONTENT_TYPE_TEMPLATE, microserviceDependency.getContentTypeTemplate());
        dependency.put(ServiceConfigurationProperties.HEADERS, microserviceDependency.getHeaders());
        dependency.put(ServiceConfigurationProperties.VERSION, microserviceDependency.getVersion());
        return dependency;
    }

}
