package com.ofg.infrastructure

import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

/**
 * Component used by tests that has multiple dependencies: RestOperations and ServiceRestClient
 *
 * Note that this class is created only for test and is located in test scope (not packaged into jar)!
 */
class ComponentWithMultipleDependencies {

    private ServiceRestClient serviceRestClient
    private RestOperations restOperations

    @Autowired
    ComponentWithMultipleDependencies(ServiceRestClient serviceRestClient,
                       @Qualifier("CustomApplicationQualifier") RestOperations restOperations) {
        this.serviceRestClient = serviceRestClient
        this.restOperations = restOperations
    }

    boolean hasDependenciesInjectedCorrectly() {
        return nonNullDependencies() && restOperationsImplementedBySpring()
    }

    private boolean restOperationsImplementedBySpring() {
        return restOperations.getClass() == RestTemplate
    }

    private boolean nonNullDependencies() {
        return serviceRestClient != null && restOperations != null
    }
}
