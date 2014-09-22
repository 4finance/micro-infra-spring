package com.ofg.infrastructure

import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

class ComponentWithTwoDifferentRestOperationsImplementations {

    private ServiceRestClient serviceRestClient
    private RestOperations restOperations

    @Autowired
    ComponentWithTwoDifferentRestOperationsImplementations(ServiceRestClient serviceRestClient,
                       @Qualifier("CustomApplicationQualifier") RestOperations restOperations) {
        this.serviceRestClient = serviceRestClient
        this.restOperations = restOperations
    }

    boolean hasDependenciesInjectedCorrectly() {
        return nonNullDependencies() && qualifierAnnotatedRestOperationsWasInjected()
    }

    private boolean qualifierAnnotatedRestOperationsWasInjected() {
        return restOperations.getClass() == RestTemplate
    }

    private boolean nonNullDependencies() {
        return serviceRestClient != null && restOperations != null
    }
}
