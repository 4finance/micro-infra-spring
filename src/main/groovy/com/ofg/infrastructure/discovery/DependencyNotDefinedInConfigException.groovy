package com.ofg.infrastructure.discovery

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
@InheritConstructors
class DependencyNotDefinedInConfigException extends RuntimeException {
}
