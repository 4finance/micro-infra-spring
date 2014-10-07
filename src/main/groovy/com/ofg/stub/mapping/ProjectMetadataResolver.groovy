package com.ofg.stub.mapping

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class ProjectMetadataResolver {
    static List<ProjectMetadata> resolveFromMetadata(File metadata) {
        List<ProjectMetadata> projects = []
        new JsonSlurper().parse(metadata).each { context, projectPaths ->
            projectPaths.each { projectPath ->
                projects << new ProjectMetadata(getProjectName(metadata), projectPath, context)
            }
        }
        return projects
    }

    static List<ProjectMetadata> resolveAllProjectsFromRepository(StubRepository stubRepository, String context) {
        checkIfContextIsProvided(context)
        return collectProjectsFromProjectsFolder(stubRepository.projectMetadataRepository.path, context)
    }

    private static String getProjectName(File metadata) {
        return metadata.name.substring(0, metadata.name.lastIndexOf('.'))
    }

    private static void checkIfContextIsProvided(String context) {
        if (!context) {
            throw new NoContextProvidedException()
        }
    }

    private static List<ProjectMetadata> collectProjectsFromProjectsFolder(File projectFolder, String context) {
        List<ProjectMetadata> projects = []
        projectFolder.eachFileRecurse { File file ->
            if (!file.isDirectory()) {
                String relativePathToDirectory = new File(projectFolder.toURI().relativize(file.parentFile.toURI()).toString())
                ProjectMetadata project = new ProjectMetadata(file.name.replaceFirst(~/\.[^\.]+$/, ''), relativePathToDirectory, context)
                log.debug("Adding project [$project] to list of found projects")
                projects << project
            }
        }
        return projects
    }
}
