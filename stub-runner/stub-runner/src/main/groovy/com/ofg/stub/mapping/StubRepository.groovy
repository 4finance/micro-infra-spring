package com.ofg.stub.mapping

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic
class StubRepository {
    @PackageScope static final String DEFAULT_DESCRIPTORS_DIRECTORY = 'mappings'

    private final DescriptorRepository descriptorRepository

    StubRepository(File repositoryRoot) {
        File descriptorDirectory = new File(repositoryRoot, DEFAULT_DESCRIPTORS_DIRECTORY)
        descriptorRepository = new DescriptorRepository(descriptorDirectory)
    }

    List<MappingDescriptor> getProjectDescriptors(ProjectMetadata project) {
        return descriptorRepository.getProjectDescriptors(project)
    }
}
