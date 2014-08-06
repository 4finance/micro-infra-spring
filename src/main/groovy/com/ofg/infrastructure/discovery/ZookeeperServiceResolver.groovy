package com.ofg.infrastructure.discovery
import com.google.common.base.Optional
import groovy.transform.TypeChecked
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceProvider

@TypeChecked
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
    Optional<String> getUrl(String dependency) {
        ServiceProvider serviceProvider = services[dependency]
        checkIfDependencyNotDefinedInConfig(serviceProvider, dependency)
        return Optional.fromNullable(serviceProvider?.instance?.buildUriSpec())
    }

    private void checkIfDependencyNotDefinedInConfig(ServiceProvider serviceProvider, String dependency) {
        if (!serviceProvider) {
            throw new DependencyNotDefinedInConfigException("$dependency dependency not found")
        }
    }
}
