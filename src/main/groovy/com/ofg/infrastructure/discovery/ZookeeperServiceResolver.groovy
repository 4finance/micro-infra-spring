package com.ofg.infrastructure.discovery

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceProvider

@CompileStatic
class ZookeeperServiceResolver implements ServiceResolver {
    private final ServiceConfigurationResolver serviceConfigurationResolver
    private final ServiceDiscovery serviceDiscovery
    private final Map<String, ServiceProvider> services = [:]

    ZookeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery
        this.serviceConfigurationResolver = serviceConfigurationResolver
    }

    @Override
    void start() {
        serviceConfigurationResolver.dependencies.each { Object serviceName, Object dependencyConfig ->
            String path = dependencyConfig['path']
            ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(path).build()
            serviceProvider.start()
            services[serviceName as String] = serviceProvider
        }
    }

    @Override
    void close() {
        services.values().each {
            it.close()
        }
    }

    @Override
    Optional<String> getUrl(String service) {
        return Optional.fromNullable(resolveServiceAddress(service))
    }

    @Override
    String fetchUrl(String service) {
        String serviceAddress = resolveServiceAddress(service)
        if (serviceAddress) {
            return serviceAddress
        } else {
            throw new ServiceUnavailableException(service)
        }
    }

    @Override
    Set<String> fetchCollaboratorsNames() {
        return services.keySet()
    }

    private String resolveServiceAddress(String service) {
        ServiceProvider serviceProvider = services[service]
        checkIfDependencyNotDefinedInConfig(serviceProvider, service)
        return serviceProvider?.instance?.buildUriSpec()
    }

    private void checkIfDependencyNotDefinedInConfig(ServiceProvider serviceProvider, String dependency) {
        if (!serviceProvider) {
            throw new DependencyNotDefinedInConfigException("$dependency dependency not found")
        }
    }
}
