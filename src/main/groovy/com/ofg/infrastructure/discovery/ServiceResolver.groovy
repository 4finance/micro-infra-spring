package com.ofg.infrastructure.discovery

import groovy.transform.PackageScope
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceProvider

class ServiceResolver {

    private final ServiceConfigurationResolver serviceConfigurationResolver
    private final ServiceDiscovery serviceDiscovery
    private final Map<String, ServiceProvider> services = [:] 

    ServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery
        this.serviceConfigurationResolver = serviceConfigurationResolver        
    }

    @PackageScope void startServiceProviders() {
        serviceConfigurationResolver.dependencies.each { String serviceName, String dependencyConfig ->
            ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(dependencyConfig).build()
            serviceProvider.start()
            services[serviceName] = serviceProvider
        }
    }
    
    @PackageScope void stopServiceProviders() {
        services.values().each {
            it.close()
        }    
    }
    
    Optional<String> getUrl(String dependency) {
        ServiceProvider serviceProvider = services[dependency]
        checkIfDependencyNotMissing(serviceProvider, dependency)
        return Optional.ofNullable(serviceProvider?.instance?.buildUriSpec())
    }

    private void checkIfDependencyNotMissing(ServiceProvider serviceProvider, String dependency) {
        if (!serviceProvider) {
            throw new DependencyNotDefinedException("$dependency dependency not found")
        }
    }

}
