package com.ofg.stub.mapping

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class ProjectMetadataResolver {

    private static final Closure PASSING_THROUGH_FILTER = { String context, Collection<String> projectPaths -> true }

    static Collection<ProjectMetadata> resolveFromMetadata(File metadata) {
        return resolveFromMetadataWithFilter(metadata, PASSING_THROUGH_FILTER)
    }

    private static Collection<ProjectMetadata> resolveFromMetadataWithFilter(File metadata, Closure<Boolean> projectMatchesRequirements) {
        Set<ProjectMetadata> projects = []
        new JsonSlurper().parse(metadata)
                         .findAll { context, projectPaths -> projectMatchesRequirements(context, projectPaths) }
                         .each { context, projectPaths ->
                                     projectPaths.each { projectPath ->
                                         projects << new ProjectMetadata(getProjectName(metadata), projectPath, context)
                                     }
                         }
        return projects
    }


    static Collection<ProjectMetadata> resolveAllProjectsFromRepository(StubRepository stubRepository, String context) {
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

    private static Collection<ProjectMetadata> collectProjectsFromProjectsFolder(File projectFolder, String providedContext) {
        Set<ProjectMetadata> projects = []
        projectFolder.eachFileRecurse { File file ->
            if (!file.isDirectory()) {
                Collection<ProjectMetadata> resolvedProjects = resolveFromMetadataWithFilter(file, passMatchingProjects(providedContext))
                if (resolvedProjects.empty) {
                    log.debug("No matches found for context [$providedContext] and file [$file]")
                    return
                }
                projects << appendProject(resolvedProjects)
            }
        }
        return projects
    }

    private static ProjectMetadata appendProject(Collection<ProjectMetadata> resolvedProjects) {
        ProjectMetadata resolvedProject = resolvedProjects.first()
        log.debug("Adding project [$resolvedProject] to list of found projects")
        return resolvedProject
    }

    private static Closure passMatchingProjects(String providedContext) {
        return { String context, Collection<String> projectPaths -> context == providedContext }
    }

}
