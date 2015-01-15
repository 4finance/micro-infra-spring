package com.ofg.infrastructure.discovery

import com.google.common.base.Function
import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.util.LoadBalancerType
import com.ofg.infrastructure.discovery.util.ProviderStrategyFactory
import groovy.transform.CompileStatic
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ProviderStrategy
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
    private final ProviderStrategyFactory providerStrategyFactory

    ZookeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver,
                             ServiceDiscovery serviceDiscovery,
                             CuratorFramework curatorFramework,
                             ProviderStrategyFactory providerStrategyFactory) {
        this.serviceDiscovery = serviceDiscovery
        this.serviceConfigurationResolver = serviceConfigurationResolver
        this.curatorFramework = curatorFramework
        this.providerStrategyFactory = providerStrategyFactory
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
                .providerStrategy(loadBalancerStrategyFor(servicePath))
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
    Set<ServicePath> fetchMyDependencies() {
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
    Set<ServicePath> fetchAllDependencies() {
        return findLeavesOf(serviceConfigurationResolver.basePath)
    }

    @Override
    Optional<String> getUrl(String service) {
        return getUri(toPath(service))
                .transform({uri -> uri.toString()} as Function)
    }

    @Override
    String fetchUrl(String service) throws ServiceUnavailableException {
        return fetchUri(toPath(service)).toString()
    }

    @Override
    Set<String> fetchCollaboratorsNames() {
        return serviceConfigurationResolver
                .dependencies
                .keySet()
                .collect{it.toString()}
                .toSet()
    }

    private ServicePath toPath(String service) {
        return resolveAlias(new ServiceAlias(service))
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

    private ProviderStrategy loadBalancerStrategyFor(ServicePath service) {
        LoadBalancerType type = serviceConfigurationResolver.getLoadBalancerTypeOf(service)
        return providerStrategyFactory.createProviderStrategy(type)
    }

}
