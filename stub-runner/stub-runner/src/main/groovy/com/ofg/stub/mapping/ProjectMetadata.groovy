package com.ofg.stub.mapping

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames = true)
@EqualsAndHashCode
class ProjectMetadata {
    final String projectName
    final String projectRelativePath
    final String context

    ProjectMetadata(String projectName, String projectRelativePath, String context) {
        this.projectName = projectName
        this.projectRelativePath = projectRelativePath
        this.context = context
    }

    String getPathWithContext() {
        return "$context/$projectRelativePath"
    }
}
