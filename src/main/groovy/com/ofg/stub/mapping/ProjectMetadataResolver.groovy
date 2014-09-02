package com.ofg.stub.mapping

import groovy.json.JsonSlurper

class ProjectMetadataResolver {
    static List<ProjectMetadata> resolveFromMetadata(File metadata) {
        List<ProjectMetadata> projects = []
        new JsonSlurper().parse(metadata).each { context, projectNames ->
            projectNames.each {
                projects << new ProjectMetadata(it, context)
            }
        }
        return projects
    }

    static List<ProjectMetadata> resolveAllProjectsFromRepository(DescriptorRepository repository, String context) {
        if (!context) {
            throw new NoContextProvidedException()
        }
        List<ProjectMetadata> projects = []
        new File(repository.location).eachDirRecurse { File dir ->
            boolean directoryHasChildren = dir.listFiles().any { it.directory }
            if (!directoryHasChildren) {
                String relativePathToDirectory = new File(repository.location.relativize(dir.toURI()).toString())
                projects << new ProjectMetadata(relativePathToDirectory, context)
            }
        }
        return projects
    }
}
