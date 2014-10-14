package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@InheritConstructors
@CompileStatic
class ResponseException extends RuntimeException {}
