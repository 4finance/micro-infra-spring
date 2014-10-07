package com.ofg.stub.mapping

import spock.lang.Specification

class ProjectMetadataParserSpec extends Specification {
    public static final String PL_CONTEXT = 'pl'
    public static final File METADATA = new File('src/test/resources/repository/projects/metadata.json')
    public static final ProjectMetadata PL_HELLO_PROJECT = new ProjectMetadata('metadata', 'com/ofg/hello', PL_CONTEXT)
    public static final ProjectMetadata PL_BYE_PROJECT = new ProjectMetadata('metadata', 'com/ofg/bye', PL_CONTEXT)
    public static final ProjectMetadata LV_PING_PROJECT = new ProjectMetadata('metadata', 'com/ofg/ping', 'lv')

    public static final ProjectMetadata PL_DESCRIPTOR_PROJECT = new ProjectMetadata('descriptor', '', PL_CONTEXT)
    public static final ProjectMetadata PL_BROKER_PROJECT = new ProjectMetadata('brokers', 'brokers', PL_CONTEXT)
    public static final ProjectMetadata PL_ANOTHER_DESCRIPTOR_PROJECT = new ProjectMetadata('anotherDescriptor', 'brokers/nested', PL_CONTEXT)
    public static final File ROOT_STUB_REPOSITORY = new File('src/test/resources/anotherRepository/')

    def 'should parse project metadata'() {
        given:
            List<ProjectMetadata> expectedProjects = [PL_HELLO_PROJECT, PL_BYE_PROJECT, LV_PING_PROJECT]
        when:
            List<ProjectMetadata> projects = ProjectMetadataResolver.resolveFromMetadata(METADATA)
        then:
            projects.size() == expectedProjects.size()
            projects.containsAll(expectedProjects)
    }

    def 'should parse project metadata files that resides under default folder'() {
        given:
            StubRepository repository = new StubRepository(ROOT_STUB_REPOSITORY)
            List<ProjectMetadata> expectedProjects = [PL_DESCRIPTOR_PROJECT, PL_BROKER_PROJECT, PL_ANOTHER_DESCRIPTOR_PROJECT]
        when:
            List<ProjectMetadata> projects = ProjectMetadataResolver.resolveAllProjectsFromRepository(repository, PL_CONTEXT)
        then:
            projects.size() == expectedProjects.size()
            projects.containsAll(expectedProjects)
    }

    def 'should throw exception for empty context when creating meta data for all stubs'() {
        given:
            String emptyContext = ''
            StubRepository repository = new StubRepository(ROOT_STUB_REPOSITORY)
        when:
            ProjectMetadataResolver.resolveAllProjectsFromRepository(repository, emptyContext)
        then:
            thrown(NoContextProvidedException)
    }
}
