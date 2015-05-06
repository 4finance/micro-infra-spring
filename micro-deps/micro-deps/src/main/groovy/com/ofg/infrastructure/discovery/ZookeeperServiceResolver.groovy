package com.ofg.infrastructure.discovery
import com.google.common.base.Function
import com.google.common.base.Optional
import com.google.common.base.Predicate
import com.ofg.infrastructure.discovery.util.CollectionUtils
import com.ofg.infrastructure.discovery.util.LoadBalancerType
import com.ofg.infrastructure.discovery.util.ProviderStrategyFactory
import groovy.transform.CompileStatic
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ProviderStrategy
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.ServiceProvider

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import static com.google.common.collect.Collections2.transform
import static com.ofg.infrastructure.discovery.util.CollectionUtils.flatten
import static com.ofg.infrastructure.discovery.util.CollectionUtils.toSet

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
        for(ServiceProvider serviceProvider : providersCache.values()) {
            serviceProvider.close()
        }
    }

    @Override
    ServicePath resolveAlias(ServiceAlias alias) {
        MicroserviceConfiguration.Dependency dependencyConfig = CollectionUtils.find(serviceConfigurationResolver.dependencies, new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.serviceAlias == alias
            }
        })
        if(!dependencyConfig)
            throw new NoSuchElementException(alias.name + ' is not our dependency, available: ' + String.valueOf(serviceConfigurationResolver.dependencies))
        return dependencyConfig.servicePath
    }

    @Override
    Optional<URI> getUri(ServicePath service) {
        return Optional.fromNullable(resolveServiceAddress(service))
    }

    @Override
    Set<URI> fetchAllUris(ServicePath service) {
        Collection<ServiceInstance> allInstances = providerFor(service).allInstances
        Collection<URI> uris = transform(allInstances, new Function<ServiceInstance, URI>() {
            @Override
            URI apply(ServiceInstance input) {
                return URI.create(input.buildUriSpec())
            }
        })
        return toSet(uris)
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
        return toSet(
                transform(
                        serviceConfigurationResolver.dependencies,
                        new Function<MicroserviceConfiguration.Dependency, ServicePath>() {
                            @Override
                            ServicePath apply(MicroserviceConfiguration.Dependency input) {
                                return input.servicePath
                            }
                        }
                )
        )
    }

    @Override
    Set<ServicePath> fetchAllDependencies() {
        return findLeavesOf(serviceConfigurationResolver.basePath)
    }

    @Override
    Optional<String> getUrl(String service) {
        return getUri(toPath(service))
                .transform(new Function<URI, String>() {
            @Override
            String apply(URI input) {
                return input.toString()
            }
        })
    }

    @Override
    String fetchUrl(String service) throws ServiceUnavailableException {
        return fetchUri(toPath(service)).toString()
    }

    @Override
    Set<String> fetchCollaboratorsNames() {
        return toSet(
                transform(
                        serviceConfigurationResolver.dependencies,
                        new Function<MicroserviceConfiguration.Dependency, String>() {
                            @Override
                            String apply(MicroserviceConfiguration.Dependency input) {
                                return input.serviceAlias.name
                            }
                        }
                )
        )
    }

    private ServicePath toPath(String service) {
        return resolveAlias(new ServiceAlias(service))
    }

    private Set<ServicePath> findLeavesOf(String root) {
        if (isServiceName(root)) {
            return toSet(Arrays.asList(new ServicePath(stripBasePath(root))))
        }
        List<String> children = curatorFramework.children.forPath("/$root")
        return toSet(
                flatten(
                    transform(
                            children,
                            new Function<String, Set<ServicePath>>() {
                                @Override
                                Set<ServicePath> apply(String input) {
                                    return findLeavesOf(root + '/' + input)
                                }
                            }
                    ), ServicePath.class
                )
            )
    }

    private final boolean isServiceName(String path) {
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
        ServiceInstance instance = providerFor(service).instance
        if (instance == null) {
            return null
        }
        return URI.create(instance.buildUriSpec())
    }

    private ServiceProvider providerFor(ServicePath service) {
        ServiceProvider serviceProvider = providersCache[service]
        if (serviceProvider == null) {
            serviceProvider = startedServiceProvider(service)
            ServiceProvider previousProvider = providersCache.putIfAbsent(service, serviceProvider)
            if( previousProvider != null) {
                previousProvider.close()
            }
        }
        return serviceProvider
    }

    private ProviderStrategy loadBalancerStrategyFor(ServicePath service) {
        LoadBalancerType type = serviceConfigurationResolver.getLoadBalancerTypeOf(service)
        return providerStrategyFactory.createProviderStrategy(type)
    }

}
