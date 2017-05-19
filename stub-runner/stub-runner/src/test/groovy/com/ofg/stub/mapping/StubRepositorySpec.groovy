package com.ofg.stub.mapping

import spock.lang.Specification

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

class StubRepositorySpec extends Specification {
    public static final File REPOSITORY_LOCATION = new File('src/test/resources/repository')

    def 'should retrieve all descriptors for given project (both from main and realm specific contexts)'() {
        given:
            StubRepository repository = new StubRepository(REPOSITORY_LOCATION)
            ProjectMetadata projectMetadata = new ProjectMetadata('bye', 'com/ofg/bye', 'pl')
            List<MappingDescriptor> expectedDescriptors = prepareStubDescriptors(descriptorMappingsSubPath)
        when:
            List<MappingDescriptor> descriptors = repository.getProjectDescriptors(projectMetadata)
        then:
            descriptors.size() == expectedDescriptors.size()
            descriptors.containsAll(expectedDescriptors)
        where:
            descriptorMappingsSubPath << ['mappings', 'some/deeper/placed/mappings']
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

    private prepareStubDescriptors(String descriptorMappingsSubPath) {
        File mappingsRepositoryLocation = new File("$REPOSITORY_LOCATION/${descriptorMappingsSubPath}")
        MappingDescriptor mainByeStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'com/ofg/bye/bye.json'))
        MappingDescriptor adminStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'com/ofg/bye/admin/admin.json'))
        MappingDescriptor plByeStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'pl/com/ofg/bye/pl_bye.json'), REALM_SPECIFIC)
        MappingDescriptor plOverriddenByeStubDescriptor = new MappingDescriptor(new File(mappingsRepositoryLocation, 'pl/com/ofg/bye/pl_overridden_bye.json'), REALM_SPECIFIC)
        return [mainByeStubDescriptor, adminStubDescriptor, plByeStubDescriptor, plOverriddenByeStubDescriptor]
    }
}
