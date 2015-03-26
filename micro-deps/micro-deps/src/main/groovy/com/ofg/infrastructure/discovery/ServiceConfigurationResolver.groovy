package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.util.LoadBalancerType
import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH
import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.REQUIRED

class ServiceConfigurationResolver {

    final String basePath
    private final Object parsedConfiguration
    final MicroserviceConfiguration microserviceConfiguration
    private static final Map EMPTY_MAP = [:]

    ServiceConfigurationResolver(String configuration) throws InvalidMicroserviceConfigurationException {
        (basePath, parsedConfiguration, microserviceConfiguration) = parseConfig(configuration)
    }

    private static List parseConfig(String config) {
        Map json = new JsonSlurper().parseText(config)
        checkThatJsonHasOneRootElement(json)
        String basePath = json.keySet().first()
        def serviceMetadata = json[basePath]
        checkThatServiceMetadataContainsValidElements(serviceMetadata)
        convertFlatDependenciesToMapFormat(serviceMetadata)
        validateDependencyEntries(serviceMetadata)
        setDefaultsForMissingOptionalElements(serviceMetadata)
        serviceMetadata.dependencies = convertDependenciesToMapWithNameAsKey(serviceMetadata.dependencies)
        ServicePath servicePath = new ServicePath(serviceMetadata.this as String)
        List<MicroserviceConfiguration.Dependency> dependencies = serviceMetadata.dependencies.collect {
            String alias = it.key
            String path = it.value.path ?: it.value
            boolean required = it.value?.required ?: false
            LoadBalancerType loadBalancerType = getLoadBalancerType(serviceMetadata.dependencies, new ServicePath(path))
            String contentTypeTemplate = it.value?.contentTypeTemplate ?: StringUtils.EMPTY
            String version = it.value?.version ?: StringUtils.EMPTY
            Map<String, String> headers = it.value?.headers?.entrySet()?.collectEntries {
                [(it.key): it.value]
            } ?: [:]
            new MicroserviceConfiguration.Dependency(new ServiceAlias(alias), new ServicePath(path), required, loadBalancerType, contentTypeTemplate, version, headers)
        }
        MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(servicePath, dependencies)
        return [basePath, serviceMetadata, microserviceConfiguration]
    }

    private static void checkThatJsonHasOneRootElement(Map json) {
        if (json.size() != 1) {
            throw new InvalidMicroserviceConfigurationException('multiple root elements')
        }
    }

    private static Map convertDependenciesToMapWithNameAsKey(Map dependencies) {
        Map convertedDependencies = [:]
        dependencies.each {convertedDependencies[it.key] = it.value}
        return convertedDependencies
    }

    private static void checkThatServiceMetadataContainsValidElements(serviceMetadata) {
        if (!(serviceMetadata.this && serviceMetadata.this instanceof String)) {
            throw new InvalidMicroserviceConfigurationException('invalid or missing "this" element')
        }
        if (serviceMetadata.dependencies && !(serviceMetadata.dependencies instanceof Map)) {
            throw new InvalidMicroserviceConfigurationException('invalid "dependencies" element')
        }
    }

    private static void convertFlatDependenciesToMapFormat(serviceMetadata) {
        serviceMetadata.dependencies.each {
            if (it.value instanceof String) {
                it.value = [(PATH):it.value]
            }
        }
    }

    private static void validateDependencyEntries(serviceMetadata) {
        List invalidDependenciesNames = serviceMetadata.dependencies
                .findAll { !(it.value instanceof Map) }
                .collect { key, value -> key }
        if (!invalidDependenciesNames.isEmpty()) {
            throw new InvalidMicroserviceConfigurationException("following dependencies have invalid format: " +
                    " $invalidDependenciesNames (Check documentation for details.)")
        }
    }

    private static void setDefaultsForMissingOptionalElements(serviceMetadata) {
        if (serviceMetadata.dependencies == null) {
            serviceMetadata.dependencies = [:]
        }
        serviceMetadata.dependencies.each {
            if (!it.value[REQUIRED]) {
                it.value[REQUIRED] == false
            }
        }
    }

    String getMicroserviceName() {
        return microserviceConfiguration.servicePath.path
    }

    List<MicroserviceConfiguration.Dependency> getDependencies() {
        return microserviceConfiguration.dependencies
    }

    MicroserviceConfiguration.Dependency getDependencyForName(String serviceName) {
        return microserviceConfiguration.dependencies.find { it.serviceAlias.name == serviceName }
    }

    LoadBalancerType getLoadBalancerTypeOf(ServicePath dependencyPath) {
        return getLoadBalancerType(parsedConfiguration.dependencies, dependencyPath)
    }

    private static LoadBalancerType getLoadBalancerType(Map dependencies, ServicePath dependencyPath) {
        Map dependencyConfig = getDependencyConfigByPath(dependencies, dependencyPath.path)
        String strategyName = dependencyConfig['load-balancer']
        return LoadBalancerType.fromName(nonNullStrategyName(strategyName))
    }

    private static String nonNullStrategyName(String strategyName) {
        strategyName ? strategyName.toUpperCase() : ''
    }

    private static Map getDependencyConfigByPath(Map dependencies, String dependencyPath) {
        dependencies.findResult(EMPTY_MAP) { if (it.value['path'] == dependencyPath) return it.value }
    }
}
