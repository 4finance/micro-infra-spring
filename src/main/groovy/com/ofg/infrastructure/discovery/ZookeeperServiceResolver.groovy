package com.ofg.infrastructure.discovery

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceProvider

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@CompileStatic
class ZookeeperServiceResolver implements ServiceResolver {
    private final ServiceConfigurationResolver serviceConfigurationResolver
    private final ServiceDiscovery serviceDiscovery
    private final ConcurrentMap<ServicePath, ServiceProvider> providersCache = new ConcurrentHashMap<>()
    private final CuratorFramework curatorFramework

    ZookeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver,
                             ServiceDiscovery serviceDiscovery,
                             CuratorFramework curatorFramework) {
        this.serviceDiscovery = serviceDiscovery
        this.serviceConfigurationResolver = serviceConfigurationResolver
        this.curatorFramework = curatorFramework
    }

    @Override
    void start() {}

    @Override
    void close() {
        providersCache.values()*.close()
    }

    @Override
    ServicePath resolveAlias(ServiceAlias alias) {
        Object dependencyConfig = serviceConfigurationResolver.dependencies[alias.name]
        if(!dependencyConfig)
            throw new NoSuchElementException("${alias.name} is not our dependency, available: ${serviceConfigurationResolver.dependencies.keySet()}")
        return getPathFromDependencyConfig(dependencyConfig)
    }

    @Override
    Optional<URI> getUri(ServicePath service) {
        return Optional.fromNullable(resolveServiceAddress(service))
    }

    @Override
    Set<URI> fetchAllUris(ServicePath service) {
        return providerFor(service)
                .allInstances*.buildUriSpec()
                .collect { it.toURI() }
                .toSet()
    }

    private ServiceProvider startedServiceProvider(ServicePath servicePath) {
        ServiceProvider serviceProvider = serviceDiscovery
                .serviceProviderBuilder()
                .serviceName(servicePath.path)
                .build()
        serviceProvider.start()
        return serviceProvider
    }


    @Override
    URI fetchUri(ServicePath service) {
        URI serviceAddress = resolveServiceAddress(service)
        if (serviceAddress) {
            return serviceAddress
        } else {
            throw new ServiceUnavailableException(service.path)
        }
    }

    @Override
    Set<ServicePath> fetchCollaboratorsNames() {
        serviceConfigurationResolver
                .dependencies
                .values()
                .collect { getPathFromDependencyConfig(it) }
                .toSet()
    }

    private static ServicePath getPathFromDependencyConfig(dependencyConfig) {
        return new ServicePath(dependencyConfig['path'] as String)
    }

    @Override
    Set<ServicePath> fetchAllServices() {
        return findLeavesOf(serviceConfigurationResolver.basePath)
    }

    private Set<ServicePath> findLeavesOf(String root) {
        if (isServiceName(root)) {
            return [new ServicePath(stripBasePath(root))].toSet()
        }
        List<String> children = curatorFramework.children.forPath("/$root")
        return children.collect {
            findLeavesOf("$root/$it")
        }.flatten().toSet()
    }

    private final isServiceName(String path) {
        final String potentialServiceName = path.substring(serviceConfigurationResolver.basePath.length())
        try {
            return !serviceDiscovery.queryForInstances(potentialServiceName).empty
        } catch (EOFException ignored) {
            //ignored, not a service
            return false
        }
    }

    private String stripBasePath(String path) {
        return path.substring(serviceConfigurationResolver.basePath.length() + 1)
    }

    private URI resolveServiceAddress(ServicePath service) {
        return providerFor(service).instance?.buildUriSpec()?.toURI()
    }

    private ServiceProvider providerFor(ServicePath service) {
        ServiceProvider serviceProvider = providersCache[service]
        if (!serviceProvider) {
            serviceProvider = startedServiceProvider(service)
            ServiceProvider previousProvider = providersCache.putIfAbsent(service, serviceProvider)
            previousProvider?.close()

        }
        return serviceProvider
    }

}
