package com.ofg.infrastructure.discovery

import com.google.common.base.Optional as GuavaOptional
import groovy.transform.CompileStatic

@CompileStatic
class StubbedServiceResolver implements ServiceResolver {

    Map<String, String> stubbedDeps = [:]
    private final Integer wiremockPort
    private final String wiremockUrl

    StubbedServiceResolver(Integer wiremockPort,  String wiremockUrl) {
        this.wiremockPort = wiremockPort
        this.wiremockUrl = wiremockUrl
    }

    void stubDependency(String dependency, String address) {
        stubbedDeps[dependency] = address
    }

    void resetDependencies() {
        stubbedDeps.clear()
    }

    void stubDependenciesFrom(ServiceConfigurationResolver serviceConfigurationResolver) {
        serviceConfigurationResolver.dependencies.each {
            stubDependency(it.key, "http://$wiremockUrl:$wiremockPort/${it.key}")
        }
    }

    @Override
    GuavaOptional<String> getUrl(String dependency) {
        return GuavaOptional.fromNullable(stubbedDeps[dependency])
    }

    @Override
    void start() { }

    @Override
    void close() { }

}
