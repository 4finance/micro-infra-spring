package com.ofg.infrastructure.discovery

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic

/**
 * Class providing additional information about a microservice instance.
 */
@CompileStatic
class InstanceDetails {

    final List<String> dependencies

    InstanceDetails() {
        this.dependencies = Collections.emptyList()
    }

    /**
     * Creates new instance of the class with information about microservice dependencies.
     *
     * @param dependencies list of dependencies' names of our microservice
     */
    InstanceDetails(List<String> dependencies) {
        this.dependencies = ImmutableList.copyOf(dependencies)
    }
}
