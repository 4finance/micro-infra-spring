package com.ofg.stub.mapping

import groovy.json.JsonSlurper

class ProjectMetadataParser {
    static List<ProjectMetadata> parseMetadata(File metadata) {
        List<ProjectMetadata> projects = []
        new JsonSlurper().parse(metadata).each { context, projectNames ->
            projectNames.each {
                projects << new ProjectMetadata(it, context)
            }
        }
        return projects
    }
}
