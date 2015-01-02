package com.ofg.stub.mapping

import spock.lang.Specification

class ProjectMetadataSpec extends Specification {

    def 'should build full path with context'() {
        given:
            String projectName = 'ping'
            String projectPath = 'com/ofg/ping'
            String context = 'lv'
            ProjectMetadata projectMetadata = new ProjectMetadata(projectName, projectPath, context)
        expect:
            projectMetadata.pathWithContext == "$context/$projectPath"
    }
}
