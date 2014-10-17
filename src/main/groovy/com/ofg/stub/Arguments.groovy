package com.ofg.stub

import com.ofg.stub.mapping.ProjectMetadata
import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames = true)
class Arguments {
    final String repositoryPath
    final String projectRelativePath
    final Integer testingZookeeperPort
    final Integer minPortValue
    final Integer maxPortValue
    final String context
    final String localZookeeperPath
    final List<ProjectMetadata> projects

    Arguments(String repositoryPath, String projectRelativePath, Integer testingZookeeperPort, Integer minPortValue, Integer maxPortValue, String context, String localZookeeperPath, List<ProjectMetadata> projects) {
        this.repositoryPath = repositoryPath
        this.projectRelativePath = projectRelativePath
        this.testingZookeeperPort = testingZookeeperPort
        this.minPortValue = minPortValue
        this.maxPortValue = maxPortValue
        this.context = context
        this.projects = projects
        this.localZookeeperPath = localZookeeperPath
    }

    Arguments(String repositoryPath, String projectRelativePath, Integer testingZookeeperPort, Integer minPortValue, Integer maxPortValue, String context, String localZookeeperPath) {
        this(repositoryPath, projectRelativePath, testingZookeeperPort, minPortValue, maxPortValue, context, localZookeeperPath, null)
    }

}
