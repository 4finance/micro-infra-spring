package com.ofg.stub

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import groovy.transform.CompileStatic

@CompileStatic
class DescriptorToCollaborators {

    static Collaborators fromDeprecatedMicroserviceDescriptor(ServiceConfigurationResolver serviceConfigurationResolver) {
        return new Collaborators(serviceConfigurationResolver.basePath,
                serviceConfigurationResolver.dependencies.collect {
                    it.servicePath.path
                }, serviceConfigurationResolver.dependencies.collectEntries {
            [it.servicePath.path, it.stubs]
        })
    }
}
