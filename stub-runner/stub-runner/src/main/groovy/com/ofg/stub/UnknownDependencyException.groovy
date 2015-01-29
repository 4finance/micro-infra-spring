package com.ofg.stub

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
@InheritConstructors
class UnknownDependencyException extends RuntimeException {

    UnknownDependencyException(String dependencyPath) {
        super("Unknown dependency with path '$dependencyPath'")
    }

}
