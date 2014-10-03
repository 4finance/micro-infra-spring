package com.ofg.stub.mapping

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import static com.ofg.stub.mapping.DescriptorConstants.PROJECTS_FOLDER_NAME

@Slf4j
class ProjectMetadataResolver {
    static List<ProjectMetadata> resolveFromMetadata(File metadata) {
        List<ProjectMetadata> projects = []
        new JsonSlurper().parse(metadata).each { context, projectNames ->
            projectNames.each {
                String name = metadata.name.with {
                    return substring(0, lastIndexOf('.'))
                }
                projects << new ProjectMetadata(name, it, context)
            }
        }
        return projects
    }

    static List<ProjectMetadata> resolveAllProjectsFromRepository(DescriptorRepository repository, String context) {
        checkIfContextIsProvided(context)
        File projectFolder = new File("${repository.location.path}/$PROJECTS_FOLDER_NAME")
        checkIfProjectFolderExists(projectFolder)
        return collectProjectsFromProjectsFolder(projectFolder, context)
    }

    private static void checkIfContextIsProvided(String context) {
        if (!context) {
            throw new NoContextProvidedException()
        }
    }

    private static void checkIfProjectFolderExists(File projectFolder) {
        if (!projectFolder.exists()) {
            throw new ProjectFolderMissingException()
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
