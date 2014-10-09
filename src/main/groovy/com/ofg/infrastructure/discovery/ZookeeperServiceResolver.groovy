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
        serviceConfigurationResolver.dependencies.each { String serviceName, String dependencyConfig ->
            ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(dependencyConfig).build()
            serviceProvider.start()
            services[serviceName] = serviceProvider
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
            throw new ServiceNotFoundException("could not resolve $service address")
        }
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
