package com.ofg.stub

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Structure representing collaborators together with their base path
 */
@Immutable
@CompileStatic
class Collaborators {
    String basePath
    List<String> collaboratorsPath
}