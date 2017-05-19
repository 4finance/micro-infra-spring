package com.ofg.stub.mapping

import groovy.transform.CompileStatic

@CompileStatic
class StubRepository {
    static final String DEFAULT_DESCRIPTORS_DIRECTORY = 'mappings'

    private final DescriptorRepository descriptorRepository

    StubRepository(File repositoryRoot) {
        File descriptorDirectory = new File(repositoryRoot, DEFAULT_DESCRIPTORS_DIRECTORY)
        if (!descriptorDirectory.exists()) {
            repositoryRoot.eachDirRecurse {
                if (it.name == DEFAULT_DESCRIPTORS_DIRECTORY) {
                    descriptorDirectory = it
                }
            }
        }
        descriptorRepository = new DescriptorRepository(descriptorDirectory)
    }

    List<MappingDescriptor> getProjectDescriptors(ProjectMetadata project) {
        return descriptorRepository.getProjectDescriptors(project)
    }
}
