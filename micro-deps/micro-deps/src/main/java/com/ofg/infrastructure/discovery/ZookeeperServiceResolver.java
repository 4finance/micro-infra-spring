package com.ofg.infrastructure.discovery;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import com.ofg.infrastructure.discovery.util.ProviderStrategyFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.UriSpec;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.collect.Collections2.transform;
import static com.ofg.infrastructure.discovery.util.CollectionUtils.*;
import static java.util.Collections.singleton;

@Deprecated
public class ZookeeperServiceResolver implements ServiceResolver {
    private final ServiceConfigurationResolver serviceConfigurationResolver;
    private final ServiceDiscovery serviceDiscovery;
    private final ConcurrentMap<ServicePath, ServiceProvider> providersCache = new ConcurrentHashMap<ServicePath, ServiceProvider>();
    private final CuratorFramework curatorFramework;
    private final ProviderStrategyFactory providerStrategyFactory;

    private static final Function<ServiceInstance, URI> SERVICE_INSTANCE_TO_URI_FUNCTION = new Function<ServiceInstance, URI>() {
        @Override
        public URI apply(ServiceInstance input) {
            return URI.create(input.buildUriSpec());
        }
    };

    private static final Function<MicroserviceConfiguration.Dependency, ServicePath> DEPENDENCY_TO_SERVICE_PATH_FUNCTION = new Function<MicroserviceConfiguration.Dependency, ServicePath>() {
        @Override
        public ServicePath apply(MicroserviceConfiguration.Dependency input) {
            return input.getServicePath();
        }
    };

    public ZookeeperServiceResolver(ServiceConfigurationResolver serviceConfigurationResolver, ServiceDiscovery serviceDiscovery, CuratorFramework curatorFramework, ProviderStrategyFactory providerStrategyFactory) {
        this.serviceDiscovery = serviceDiscovery;
        this.serviceConfigurationResolver = serviceConfigurationResolver;
        this.curatorFramework = curatorFramework;
        this.providerStrategyFactory = providerStrategyFactory;
    }

    @Override
    public void start() {
    }

    @Override
    public void close() {
        for (ServiceProvider serviceProvider : providersCache.values()) {
            try {
                serviceProvider.close();
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
    }

    @Override
    public ServicePath resolveAlias(final ServiceAlias alias) {
        MicroserviceConfiguration.Dependency dependencyConfig = find(serviceConfigurationResolver.getDependencies(), new Predicate<MicroserviceConfiguration.Dependency>() {
            @Override
            public boolean apply(MicroserviceConfiguration.Dependency input) {
                return input.getServiceAlias().equals(alias);
            }
        });
        if (dependencyConfig == null) {
            throw new NoSuchElementException(alias.getName() + " is not our dependency, available: " + String.valueOf(serviceConfigurationResolver.getDependencies()));
        }
        return dependencyConfig.getServicePath();
    }

    @Override
    public Optional<URI> getUri(ServicePath service) {
        return Optional.fromNullable(resolveServiceAddress(service));
    }

    @Override
    public Set<URI> fetchAllUris(ServicePath service) {
        Collection<ServiceInstance> allInstances;
        try {
            allInstances = providerFor(service).getAllInstances();
        } catch (Exception e) {
            return new HashSet<URI>();
        }
        return toSet(transform(allInstances, SERVICE_INSTANCE_TO_URI_FUNCTION));
    }

    private ServiceProvider startedServiceProvider(ServicePath servicePath) {
        ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(servicePath.getPath()).providerStrategy(loadBalancerStrategyFor(servicePath)).build();
        try {
            serviceProvider.start();
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        return serviceProvider;
    }

    @Override
    public URI fetchUri(ServicePath servicePath) {
        URI serviceAddress = resolveServiceAddress(servicePath);
        if (serviceAddress != null) {
            return serviceAddress;
        } else {
            throw new ServiceUnavailableException(servicePath);
        }
    }

    @Override
    public Set<ServicePath> fetchMyDependencies() {
        return toSet(transform(serviceConfigurationResolver.getDependencies(), DEPENDENCY_TO_SERVICE_PATH_FUNCTION));
    }

    @Override
    public Set<ServicePath> fetchAllDependencies() {
        return findLeavesOf(serviceConfigurationResolver.getBasePath());
    }

    @Override
    public Optional<URI> getUri(ServiceAlias serviceAlias) {
        return getUri(resolveAlias(serviceAlias));
    }

    @Override
    public URI fetchUri(ServiceAlias serviceAlias) {
        return fetchUri(resolveAlias(serviceAlias));
    }

    private Set<ServicePath> findLeavesOf(final String root) {
        if (isServiceName(root)) {
            return singleton(new ServicePath(stripBasePath(root)));
        }

        List<String> children = new ArrayList<String>();
        try {
            children = curatorFramework.getChildren().forPath("/" + root);
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        return toSet(flatten(transform(children, new Function<String, Set<ServicePath>>() {
            @Override
            public Set<ServicePath> apply(String input) {
                return findLeavesOf(root + "/" + input);
            }

        }), ServicePath.class));
    }

    private boolean isServiceName(String path) {
        final String potentialServiceName = path.substring(serviceConfigurationResolver.getBasePath().length());
        try {
            return !serviceDiscovery.queryForInstances(potentialServiceName).isEmpty();
        } catch (Exception ignored) {
            //ignored, not a service
            return false;
        }
    }

    private String stripBasePath(String path) {
        return path.substring(serviceConfigurationResolver.getBasePath().length() + 1);
    }

    private URI resolveServiceAddress(ServicePath service) {
        ServiceInstance instance = null;
        try {
            instance = providerFor(service).getInstance();
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        if (instance == null) {
            return null;
        }
        String uriSpec = instance.buildUriSpec();
        if (uriSpec.isEmpty()) {
            uriSpec = new UriSpec("{scheme}://{address}:{port}").build(instance);
        }

        return URI.create(uriSpec);
    }

    private ServiceProvider providerFor(ServicePath service) {
        ServiceProvider serviceProvider = providersCache.get(service);
        if (serviceProvider == null) {
            serviceProvider = startedServiceProvider(service);
            ServiceProvider previousProvider = providersCache.putIfAbsent(service, serviceProvider);
            if (previousProvider != null) {
                try {
                    previousProvider.close();
                } catch (IOException e) {
                    Throwables.propagate(e);
                }
            }
        }
        return serviceProvider;
    }

    private ProviderStrategy loadBalancerStrategyFor(ServicePath service) {
        LoadBalancerType type = serviceConfigurationResolver.getLoadBalancerTypeOf(service);
        return providerStrategyFactory.createProviderStrategy(type);
    }
}
