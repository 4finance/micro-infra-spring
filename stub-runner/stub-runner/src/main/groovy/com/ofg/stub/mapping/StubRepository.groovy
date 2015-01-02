package com.ofg.stub.mapping

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic
class StubRepository {
    @PackageScope static final String DEFAULT_DESCRIPTORS_DIRECTORY = 'mappings'
    @PackageScope static final String DEFAULT_PROJECTS_METADATA_DIRECTORY = 'projects'

    private final DescriptorRepository descriptorRepository
    @PackageScope final ProjectMetadataRepository projectMetadataRepository

    StubRepository(File repositoryRoot) {
        File descriptorDirectory = new File(repositoryRoot, DEFAULT_DESCRIPTORS_DIRECTORY)
        descriptorRepository = new DescriptorRepository(descriptorDirectory)
        File projectMetadataDirectory = new File(repositoryRoot, DEFAULT_PROJECTS_METADATA_DIRECTORY)
        projectMetadataRepository = new ProjectMetadataRepository(projectMetadataDirectory)
    }

    List<MappingDescriptor> getProjectDescriptors(ProjectMetadata project) {
        return descriptorRepository.getProjectDescriptors(project)
    }

    URI getProjectMetadataLocation(String metadataFileName) {
        return projectMetadataRepository.getMetadataLocation(metadataFileName)
    }
}
