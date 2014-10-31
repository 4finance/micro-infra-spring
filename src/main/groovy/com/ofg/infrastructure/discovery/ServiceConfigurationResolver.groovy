package com.ofg.infrastructure.discovery

import groovy.json.JsonSlurper

class ServiceConfigurationResolver {

    final String basePath
    private final Object parsedConfiguration

    ServiceConfigurationResolver(String configuration) throws InvalidMicroserviceConfigurationException {
        (basePath, parsedConfiguration) = parseConfig(configuration)
    }

    private static List parseConfig(String config) {
        Map json = new JsonSlurper().parseText(config)
        checkThatJsonHasOneRootElement(json)
        String basePath = json.keySet().first()
        def serviceMetadata = json[basePath]
        checkThatServiceMetadataContainsValidElements(serviceMetadata)
        setDefaultsForMissingOptionalElements(serviceMetadata)
        serviceMetadata.dependencies = convertDependenciesToMapWithNameAsKey(serviceMetadata.dependencies)
        return [basePath, serviceMetadata]
    }

    private static void checkThatJsonHasOneRootElement(Map json) {
        if (json.size() != 1) {
            throw new InvalidMicroserviceConfigurationException('multiple root elements')
        }
    }

    private static Map<String, Map<String, String>> convertDependenciesToMapWithNameAsKey(List<Map<String, String>> dependencies) {
        Map<String, Map<String, String>> convertedDependencies = [:]
        dependencies.each {convertedDependencies[it['name']] = it}
        return convertedDependencies
    }

    private static void checkThatServiceMetadataContainsValidElements(serviceMetadata) {
        if (!(serviceMetadata.this && serviceMetadata.this instanceof String)) {
            throw new InvalidMicroserviceConfigurationException('invalid or missing "this" element')
        }
        if (serviceMetadata.dependencies && !(serviceMetadata.dependencies instanceof List)) {
            throw new InvalidMicroserviceConfigurationException('invalid "dependencies" element')
        }
    }

    private static void setDefaultsForMissingOptionalElements(serviceMetadata) {
        if (serviceMetadata.dependencies == null) {
            serviceMetadata.dependencies = [] as List
        }
        serviceMetadata.dependencies.each {
            if (!it['required']) {
                it['required'] == false
            }
        }
    }

    String getMicroserviceName() {
        return parsedConfiguration.this
    }

    Map<String, Map<String, String>> getDependencies() {
        return parsedConfiguration.dependencies
    }

    String getDependencyConfigByName(String dependencyName) {
        return parsedConfiguration[dependencyName]
    }

} 
