package com.ofg.stub.mapping

import spock.lang.Specification

class ProjectMetadataParserSpec extends Specification {
    static final File METADATA = new File('src/test/resources/repository/metadata.json')
    static final ProjectMetadata PL_HELLO_PROJECT = new ProjectMetadata('com/ofg/hello', 'pl')
    static final ProjectMetadata PL_BYE_PROJECT = new ProjectMetadata('com/ofg/bye', 'pl')
    static final ProjectMetadata LV_PING_PROJECT = new ProjectMetadata('com/ofg/ping', 'lv')

    def 'should parse project metadata'() {
        when:
            List<ProjectMetadata> projects = ProjectMetadataParser.parseMetadata(METADATA)

        then:
            projects.size() == 3
            projects.contains(PL_HELLO_PROJECT)
            projects.contains(PL_BYE_PROJECT)
            projects.contains(LV_PING_PROJECT)
    }
}
