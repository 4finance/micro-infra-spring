package com.ofg.infrastructure.discovery

import groovy.json.JsonSlurper

class ServiceConfigurationResolver {

    final String basePath
    private final Object parsedConfiguration

    ServiceConfigurationResolver(String configuration) throws BadConfigurationException {
        (basePath, parsedConfiguration) = parseConfig(configuration)
    }

    private static List parseConfig(String config) {
        Map json = new JsonSlurper().parseText(config)
        checkThatJsonHasOneRootElement(json)
        String basePath = json.keySet().first()
        def root = json[basePath]
        checkThatObligatoryElementsArePresent(root)
        return [basePath, json[basePath]]
    }

    private static void checkThatJsonHasOneRootElement(Map json) {
        if (json.size() != 1) {
            throw new BadConfigurationException('Microservice configuration should have exactly one root element')
        }
    }

    private static void checkThatObligatoryElementsArePresent(root) {
        if (!(root.this instanceof String && root.dependencies instanceof Map)) {
            throw new BadConfigurationException('Microservice configuration must contain "this" and "dependencies" elements')
        }
    }

    String getMicroserviceName() {
        return parsedConfiguration.this
    }
    
    Map<String, String> getDependencies() {
        return parsedConfiguration.dependencies
    }
    
    String getDependencyConfigByName(String dependencyName) {
        return parsedConfiguration[dependencyName]
    }
    
} 
