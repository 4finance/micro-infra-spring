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

    boolean hasDependenciesInjectedCorrectly() {
        return nonNullDependencies() && nonMicroInfraSpringRestOperationsWasInjected()
    }

    private boolean nonNullDependencies() {
        return serviceRestClient != null && restOperations != null
    }

    private boolean nonMicroInfraSpringRestOperationsWasInjected() {
        return restOperations.getClass() == TestRestTemplate
    }
}
