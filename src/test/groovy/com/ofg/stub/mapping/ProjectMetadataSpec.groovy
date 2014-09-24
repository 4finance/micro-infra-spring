package com.ofg.stub.mapping

import spock.lang.Specification

class ProjectMetadataSpec extends Specification {

    def 'should build full path with context'() {
        given:
            String projectName = 'com/ofg/ping'
            String context = 'lv'
            ProjectMetadata projectMetadata = new ProjectMetadata(projectName, context)
        when:
            String actualFullPath = projectMetadata.getFullPath()
        then:
            "$context/$projectName" == actualFullPath
    }
}
