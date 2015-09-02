package com.ofg.infrastructure.discovery;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.ofg.infrastructure.discovery.util.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceDiscovery;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependency;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;
import static com.ofg.infrastructure.discovery.util.CollectionUtils.flatten;
import static com.ofg.infrastructure.discovery.util.CollectionUtils.toSet;
import static java.util.Collections.singleton;

public class SpringCloudZookeeperServiceResolver implements ServiceResolver {

    private final ZookeeperDependencies zookeeperDependencies;
    private final DiscoveryClient discoveryClient;
    private final CuratorFramework curatorFramework;
    private final ZookeeperServiceDiscovery serviceDiscovery;
    private final ZookeeperDiscoveryProperties zookeeperDiscoveryProperties;

    public SpringCloudZookeeperServiceResolver(ZookeeperDependencies zookeeperDependencies,
                                               DiscoveryClient discoveryClient,
                                               CuratorFramework curatorFramework,
                                               ZookeeperServiceDiscovery serviceDiscovery,
                                               ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        this.zookeeperDependencies = zookeeperDependencies;
        this.discoveryClient = discoveryClient;
        this.curatorFramework = curatorFramework;
        this.serviceDiscovery = serviceDiscovery;
        this.zookeeperDiscoveryProperties = zookeeperDiscoveryProperties;
    }

    @Override
    public ServicePath resolveAlias(ServiceAlias alias) {
        return new ServicePath(zookeeperDependencies.getPathForAlias(alias.getName()));
    }

    @Override
    public Optional<URI> getUri(ServicePath service) {
        return getUriForPath(service);
    }

    private Optional<URI> getUriForPath(ServicePath service) {
        List<ServiceInstance> instances = getInstancesForPath(service);
        if (instances.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(Iterables.getLast(instances).getUri());
    }

    private List<ServiceInstance> getInstancesForPath(ServicePath service) {
        return discoveryClient.getInstances(service.getPathWithStartingSlash());
    }

    @Override
    public URI fetchUri(ServicePath service) {
        return getUriForPath(service).orNull();
    }

    @Override
    public Set<URI> fetchAllUris(ServicePath service) {
        List<ServiceInstance> instances = getInstancesForPath(service);
        Collection<URI> uris = Collections2.transform(instances, new Function<ServiceInstance, URI>() {
            @Override
            public URI apply(ServiceInstance input) {
                return input.getUri();
            }
        });
        return CollectionUtils.toSet(uris);
    }

    @Override
    public Set<ServicePath> fetchMyDependencies() {
        Map<String, ZookeeperDependency> dependencies = zookeeperDependencies.getDependencies();
        Set<ServicePath> servicePaths = new HashSet<>();
        for (Map.Entry<String, ZookeeperDependency> zookeeperDependencyEntry : dependencies.entrySet()) {
            servicePaths.add(new ServicePath(zookeeperDependencyEntry.getValue().getPath()));
        }
        return servicePaths;
    }

    @Override
    public Set<ServicePath> fetchAllDependencies() {
        return findLeavesOf(zookeeperDiscoveryProperties.getRoot());
    }

    private Set<ServicePath> findLeavesOf(final String root) {
        if (isServiceName(root)) {
            return singleton(new ServicePath(stripBasePath(root)));
        }

        List<String> children = new ArrayList<String>();
        try {
            String path = root;
            if (!path.startsWith("/")) {
                path += "/" + path;
            }
            children = curatorFramework.getChildren().forPath(path);
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
        final String potentialServiceName = path.substring(zookeeperDiscoveryProperties.getRoot().length());
        try {
            return !serviceDiscovery.getServiceDiscovery().queryForInstances(potentialServiceName).isEmpty();
        } catch (Exception ignored) {
            //ignored, not a service
            return false;
        }
    }

    private String stripBasePath(String path) {
        return path.substring(zookeeperDiscoveryProperties.getRoot().length() + 1);
    }

    @Override
    public Optional<URI> getUri(ServiceAlias serviceAlias) {
        return getUriForAlias(serviceAlias);
    }

    @Override
    public URI fetchUri(ServiceAlias serviceAlias) {
        return getUriForAlias(serviceAlias).orNull();
    }

    private Optional<URI> getUriForAlias(ServiceAlias service) {
        List<ServiceInstance> instances = getInstancesForAlias(service);
        if (instances.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(Iterables.getLast(instances).getUri());
    }

    private List<ServiceInstance> getInstancesForAlias(ServiceAlias service) {
        return discoveryClient.getInstances(zookeeperDependencies.getPathForAlias(service.getName()));
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }
}
