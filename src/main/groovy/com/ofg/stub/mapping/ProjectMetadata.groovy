package com.ofg.stub.mapping

import groovy.transform.EqualsAndHashCode
import groovy.transform.TypeChecked

@TypeChecked
@EqualsAndHashCode
class ProjectMetadata {
    final String projectName
    final String context

    ProjectMetadata(String projectName, String context) {
        this.projectName = projectName
        this.context = context
    }
}
