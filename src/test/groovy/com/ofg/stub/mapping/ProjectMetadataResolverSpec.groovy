package com.ofg.stub.mapping

import spock.lang.Specification

class ProjectMetadataResolverSpec extends Specification {

    private static final URL METADATA_JSON_FILE = ProjectMetadataResolverSpec.classLoader.getResource('repository/projects/metadata.json')
    private static final URL CLASSPATH_ROOT = ProjectMetadataResolverSpec.classLoader.getResource('repository')

    def 'should resolve project metadata from a JSON project descriptor when a single project is loaded'() {
        given:
            File fileWithProjectMetadata = new File(METADATA_JSON_FILE.toURI())
        when:
            Collection<ProjectMetadata> projects = ProjectMetadataResolver.resolveFromMetadata(fileWithProjectMetadata)
        then:
            projects.size() == 3
            projects.containsAll([new ProjectMetadata('metadata', 'com/ofg/ping', 'lv'),
                                  new ProjectMetadata('metadata', 'com/ofg/hello', 'pl'),
                                  new ProjectMetadata('metadata', 'com/ofg/bye', 'pl')])
    }

    def 'should resolve all project metadata when all projects from a given context are to be loaded'() {
        given:
            File classpathRoot = new File(CLASSPATH_ROOT.toURI())
        when:
            Collection<ProjectMetadata> projects = ProjectMetadataResolver.resolveAllProjectsFromRepository(new StubRepository(classpathRoot),'pl')
        then:
            projects.size() == 2
            projects.containsAll([new ProjectMetadata('metadata', 'com/ofg/hello', 'pl'),
                                  new ProjectMetadata('bye_metadata', 'com/ofg/bye', 'pl')])
    }

}
