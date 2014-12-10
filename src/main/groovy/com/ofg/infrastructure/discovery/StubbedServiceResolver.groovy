package com.ofg.infrastructure.discovery

import com.google.common.base.Optional as GuavaOptional
import groovy.transform.CompileStatic

@CompileStatic
class StubbedServiceResolver implements ServiceResolver {

    private final Map<ServicePath, URI> stubbedDeps = [:]
    private final Integer wiremockPort
    private final String wiremockUrl

    StubbedServiceResolver(Integer wiremockPort,  String wiremockUrl) {
        this.wiremockPort = wiremockPort
        this.wiremockUrl = wiremockUrl
    }

    void stubDependency(ServicePath dependency, URI address) {
        stubbedDeps[dependency] = address
    }

    void resetDependencies() {
        stubbedDeps.clear()
    }

    void stubDependenciesFrom(ServiceConfigurationResolver serviceConfigurationResolver) {
        serviceConfigurationResolver.dependencies.each {
            String dependencyName = it.value['path'] as String
            stubDependency(new ServicePath(dependencyName), 'http://$wiremockUrl:$wiremockPort/$dependencyName'.toURI())
        }
    }

    @Override
    ServicePath resolveAlias(ServiceAlias alias) {
        return new ServicePath(alias.name)
    }

    @Override
    GuavaOptional<URI> getUri(ServicePath dependency) {
        return GuavaOptional.fromNullable(stubbedDeps[dependency])
    }

    @Override
    Set<URI> fetchAllUris(ServicePath service) {
        return stubbedDeps.values().toSet()
    }

    @Override
    URI fetchUri(ServicePath service) {
        if (stubbedDeps[service]) {
            return stubbedDeps[service]
        } else {
            throw new ServiceUnavailableException(service.path)
        }
    }

    @Override
    Set<ServicePath> fetchCollaboratorsNames() {
        return stubbedDeps.keySet()
    }

    @Override
    Set<ServicePath> fetchAllServices() {
        return stubbedDeps.keySet()
    }

    @Override
    void start() { }

    @Override
    void close() { }

}
