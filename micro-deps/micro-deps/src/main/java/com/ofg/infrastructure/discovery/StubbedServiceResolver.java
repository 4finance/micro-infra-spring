package com.ofg.infrastructure.discovery;

import com.google.common.base.Optional;
import com.ofg.infrastructure.discovery.util.CollectionUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StubbedServiceResolver implements ServiceResolver {
    private final Map<ServicePath, URI> stubbedDeps = new HashMap<>();
    private final Integer wiremockPort;
    private final String wiremockUrl;

    public StubbedServiceResolver(Integer wiremockPort, String wiremockUrl) {
        this.wiremockPort = wiremockPort;
        this.wiremockUrl = wiremockUrl;
    }

    public void stubDependency(ServicePath dependency, URI address) {
        stubbedDeps.put(dependency, address);
    }

    public void resetDependencies() {
        stubbedDeps.clear();
    }

    public void stubDependenciesFrom(ServiceConfigurationResolver serviceConfigurationResolver) {
        for (MicroserviceConfiguration.Dependency it : serviceConfigurationResolver.getDependencies()) {
            String dependencyName = it.getServicePath().getPath();
            stubDependency(new ServicePath(dependencyName), URI.create("http://$wiremockUrl:$wiremockPort/$dependencyName"));
        }
    }

    @Override
    public ServicePath resolveAlias(ServiceAlias alias) {
        return new ServicePath(alias.getName());
    }

    @Override
    public Optional<URI> getUri(ServicePath dependency) {
        return Optional.fromNullable(stubbedDeps.get(dependency));
    }

    @Override
    public Set<URI> fetchAllUris(ServicePath service) {
        return CollectionUtils.toSet(stubbedDeps.values());
    }

    @Override
    public URI fetchUri(ServicePath servicePath) {
        if (stubbedDeps.containsKey(servicePath)) {
            return stubbedDeps.get(servicePath);
        } else {
            throw new ServiceUnavailableException(servicePath);
        }
    }

    @Override
    public Set<ServicePath> fetchMyDependencies() {
        return stubbedDeps.keySet();
    }

    @Override
    public Set<ServicePath> fetchAllDependencies() {
        return stubbedDeps.keySet();
    }

    @Override
    public Optional<URI> getUri(ServiceAlias serviceAlias) {
        return getUri(resolveAlias(serviceAlias));
    }

    @Override
    public URI fetchUri(ServiceAlias serviceAlias) {
        return fetchUri(resolveAlias(serviceAlias));
    }

    @Override
    public void start() {}

    @Override
    public void close() {}

    public Map<ServicePath, URI> getStubbedDeps() {
        return stubbedDeps;
    }

    public Integer getWiremockPort() {
        return wiremockPort;
    }

    public String getWiremockUrl() {
        return wiremockUrl;
    }
}
