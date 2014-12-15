package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@TypeChecked
@CompileStatic
class InstanceDetails {

    List<String> dependencies

    InstanceDetails() {

    }

    InstanceDetails(List<String> dependencies) {
        this.dependencies = dependencies
    }
}
