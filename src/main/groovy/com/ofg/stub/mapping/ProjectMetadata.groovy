package com.ofg.stub.mapping

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TypeChecked

@TypeChecked
@ToString(includeNames = true)
@EqualsAndHashCode
class ProjectMetadata {
    final String projectName
    final String projectRelativePath
    final String context
    final String pathWithContext

    ProjectMetadata(String projectName, String projectRelativePath, String context) {
        this.projectName = projectName
        this.projectRelativePath = projectRelativePath
        this.context = context
        this.pathWithContext = "$context/$projectRelativePath"
    }

}
