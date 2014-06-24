package com.ofg.infrastructure.discovery

import groovy.transform.InheritConstructors
import groovy.transform.TypeChecked

@TypeChecked
@InheritConstructors
class DependencyNotDefinedInConfigException extends RuntimeException {
}
