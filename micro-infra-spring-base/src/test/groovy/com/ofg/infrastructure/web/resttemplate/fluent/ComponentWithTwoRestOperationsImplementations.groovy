package com.ofg.infrastructure.web.resttemplate.fluent

class ComponentWithTwoRestOperationsImplementations {

    private ServiceRestClient serviceRestClient
    private TestRestTemplate restOperations

    ComponentWithTwoRestOperationsImplementations(ServiceRestClient serviceRestClient,
                                                  TestRestTemplate restOperations) {
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
