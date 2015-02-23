package com.ofg.stub

import com.ofg.stub.mapping.ProjectMetadata
import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Arguments passed to the {@link StubRunner} application
 *
 * @see StubRunner
 */
@CompileStatic
@ToString(includeNames = true)
class Arguments {
    final StubRunnerOptions stubRunnerOptions
    final String context
    final String repositoryPath
    final String serviceName
    final List<ProjectMetadata> projects

    Arguments(StubRunnerOptions stubRunnerOptions, String context, String repositoryPath, String serviceName, List<ProjectMetadata> projects = null) {
        this.stubRunnerOptions = stubRunnerOptions
        this.context = context
        this.repositoryPath = repositoryPath
        this.projects = projects
        this.serviceName = serviceName
    }

}
