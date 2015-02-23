package com.ofg.stub

import groovy.transform.Immutable

/**
 * Structure representing collaborators together with their base path
 */
@Immutable
class Collaborators {
    String basePath
    List<String> collaboratorsPath
}
