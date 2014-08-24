package com.ofg.stub.mapping

import spock.lang.Specification

import static com.ofg.stub.mapping.RepositoryMetada.EMPTY_CONTEXT

class ProjectMetadataParserSpec extends Specification {
    public static final String PL_CONTEXT = 'pl'
    public static final File METADATA = new File('src/test/resources/repository/metadata.json')
    public static final ProjectMetadata PL_HELLO_PROJECT = new ProjectMetadata('com/ofg/hello', PL_CONTEXT)
    public static final ProjectMetadata PL_BYE_PROJECT = new ProjectMetadata('com/ofg/bye', PL_CONTEXT)

    public static final ProjectMetadata LV_PING_PROJECT = new ProjectMetadata('com/ofg/ping', 'lv')
    public static final File ANOTHER_REPO_PATH = new File('src/test/resources/anotherRepository')
    public static final ProjectMetadata PL_BAR_PROJECT = new ProjectMetadata('com/ofg/bar', PL_CONTEXT)
    public static final ProjectMetadata PL_FOO_BAR_PROJECT = new ProjectMetadata('com/ofg/foo/bar', PL_CONTEXT)
    public static final File ROOT_PATH_OF_REPOSITORY = new File('src/test/resources/anotherRepository/')


    def 'should parse project metadata'() {
        when:
            List<ProjectMetadata> projects = ProjectMetadataParser.parseMetadata(new RepositoryMetada(new File('.'), METADATA))
        then:
            projects.size() == 3
            projects.contains(PL_HELLO_PROJECT)
            projects.contains(PL_BYE_PROJECT)
            projects.contains(LV_PING_PROJECT)
    }

    def 'should find jsons in folders that are the deepest in folders tree when creating meta data for all stubs'() {
        when:
            List<ProjectMetadata> projects = ProjectMetadataParser.createMetaDataForAllStubs(new RepositoryMetada(ROOT_PATH_OF_REPOSITORY, ANOTHER_REPO_PATH, PL_CONTEXT))
        then:
            projects.size() == 2
            projects.contains(PL_BAR_PROJECT)
            projects.contains(PL_FOO_BAR_PROJECT)
    }

    def 'should throw exception for empty context when creating meta data for all stubs'() {
        when:
            ProjectMetadataParser.createMetaDataForAllStubs(new RepositoryMetada(ROOT_PATH_OF_REPOSITORY, ANOTHER_REPO_PATH, EMPTY_CONTEXT))
        then:
            thrown(NoContextProvidedException)
    }
}
