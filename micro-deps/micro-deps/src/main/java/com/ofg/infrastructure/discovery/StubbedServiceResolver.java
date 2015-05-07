package com.ofg.infrastructure.discovery;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.ofg.infrastructure.discovery.util.CollectionUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StubbedServiceResolver implements ServiceResolver {
    private final Map<ServicePath, URI> stubbedDeps = new HashMap<ServicePath, URI>();
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
    public URI fetchUri(ServicePath service) {
        if (stubbedDeps.containsKey(service)) {
            return stubbedDeps.get(service);
        } else {
            throw new ServiceUnavailableException(service.getPath());
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
    public Optional<String> getUrl(String service) {
        return getUriByName(service).transform(new Function<URI, String>() {
            @Override
            public String apply(URI input) {
                return input.toString();
            }
        });
    }

    @Override
    public String fetchUrl(String service) throws ServiceUnavailableException {
        return getUriByName(service).get().toString();
    }

    private Optional<URI> getUriByName(String service) {
        return getUri(new ServicePath(service));
    }

    @Override
    public Set<String> fetchCollaboratorsNames() {
        return CollectionUtils.toSet(Collections2.transform(fetchMyDependencies(), new Function<ServicePath, String>() {
            @Override
            public String apply(ServicePath input) {
                return input.toString();
            }

        }));
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
