package com.ofg.stub.mapping

import groovy.json.JsonSlurper

class ProjectMetadataParser {
    static List<ProjectMetadata> parseMetadata(RepositoryMetada metadata) {
        List<ProjectMetadata> projects = []
        new JsonSlurper().parse(metadata.metadata).each { context, projectNames ->
            projectNames.each {
                projects << new ProjectMetadata(it, context)
            }
        }
        return projects
    }

    static List<ProjectMetadata> createMetaDataForAllStubs(RepositoryMetada repositoryMetada) {
        if (!repositoryMetada.contextProvided) {
            throw new NoContextProvidedException()
        }
        List<ProjectMetadata> projects = []
        repositoryMetada.repositoryPath.eachDirRecurse { File dir ->
            boolean directoryHasChildren = dir.listFiles().any { it.directory }
            if (!directoryHasChildren) {
                String relativePathToDirectory = new File(repositoryMetada.repositoryPath.toURI().relativize(dir.toURI()).toString())
                projects << new ProjectMetadata(relativePathToDirectory, repositoryMetada.context)
            }
        }
        return projects
    }
}
