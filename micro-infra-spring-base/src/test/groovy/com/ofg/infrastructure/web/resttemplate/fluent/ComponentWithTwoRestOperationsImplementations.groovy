package com.ofg.infrastructure.web.resttemplate.fluent

import org.springframework.web.client.RestOperations

class ComponentWithTwoRestOperationsImplementations {

    private ServiceRestClient serviceRestClient
    private RestOperations restOperations

    ComponentWithTwoRestOperationsImplementations(ServiceRestClient serviceRestClient,
                                                  RestOperations restOperations) {
        this.serviceRestClient = serviceRestClient
        this.restOperations = restOperations
    }

    void hasDependenciesInjectedCorrectly() {
        nonNullDependencies()
        nonMicroInfraSpringRestOperationsWasInjected()
    }

    private void nonNullDependencies() {
        assert serviceRestClient != null
        assert restOperations != null
    }

    private void nonMicroInfraSpringRestOperationsWasInjected() {
        assert restOperations instanceof TestRestTemplate
    }
}
