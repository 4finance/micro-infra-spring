package com.ofg.stub.mapping

import spock.lang.Specification

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

class StubRepositorySpec extends Specification {

    def 'should retrieve all descriptors for given project (both from main and realm specific contexts)'() {
        given:
            StubRepository repository = new StubRepository(new File(repositoryLocation))
            ProjectMetadata projectMetadata = new ProjectMetadata('bye', 'com/ofg/bye', 'pl')

            File mappingsRepositoryLocation = new File(repositoryLocation, descriptorMappingsSubPath)
            MappingDescriptor mainByeStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'com/ofg/bye/bye.json'))
            MappingDescriptor adminStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'com/ofg/bye/admin/admin.json'))
            MappingDescriptor plByeStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'pl/com/ofg/bye/pl_bye.json'), REALM_SPECIFIC)
            MappingDescriptor plOverriddenByeStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'pl/com/ofg/bye/pl_overridden_bye.json'), REALM_SPECIFIC)

            List<MappingDescriptor> expectedDescriptors = [mainByeStubDescriptor, adminStubDescriptor, plByeStubDescriptor, plOverriddenByeStubDescriptor]

        when:
            List<MappingDescriptor> descriptors = repository.getProjectDescriptors(projectMetadata)

        then:
            descriptors.size() == expectedDescriptors.size()
            descriptors.containsAll(expectedDescriptors)

        where:
            repositoryLocation                            | descriptorMappingsSubPath
            'src/test/resources/repository'               | 'mappings'
            'src/test/resources/deepenMappingsRepository' | 'deeper/located/mappings'
    }

    def 'should return empty list if files are missing'() {
        given:
            StubRepository repository = new StubRepository(new File('src/test/resources/anotherRepository'))
            ProjectMetadata projectMetadata = new ProjectMetadata('bye', 'com/ofg/bye', 'pl')
        when:
            List<MappingDescriptor> descriptors = repository.getProjectDescriptors(projectMetadata)
        then:
            descriptors.empty
    }

}
