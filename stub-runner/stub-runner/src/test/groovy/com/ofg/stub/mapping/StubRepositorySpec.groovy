package com.ofg.stub.mapping

import spock.lang.Specification

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

class StubRepositorySpec extends Specification {
    public static final File REPOSITORY_LOCATION = new File('src/test/resources/repository')
    public static final File MAPPINGS_REPOSITORY_LOCATION = new File("$REPOSITORY_LOCATION/mappings")
    public static final MappingDescriptor MAIN_BYE_STUB_DESCRIPTOR = new MappingDescriptor(new File(MAPPINGS_REPOSITORY_LOCATION, 'com/ofg/bye/bye.json'))
    public static final MappingDescriptor ADMIN_STUB_DESCRIPTOR = new MappingDescriptor(new File(MAPPINGS_REPOSITORY_LOCATION, 'com/ofg/bye/admin/admin.json'))
    public static final MappingDescriptor PL_BYE_STUB_DESCRIPTOR = new MappingDescriptor(new File(MAPPINGS_REPOSITORY_LOCATION, 'pl/com/ofg/bye/pl_bye.json'), REALM_SPECIFIC)
    public static final MappingDescriptor PL_OVERRIDDEN_BYE_STUB_DESCRIPTOR = new MappingDescriptor(new File(MAPPINGS_REPOSITORY_LOCATION, 'pl/com/ofg/bye/pl_overridden_bye.json'), REALM_SPECIFIC)

    def 'should retrieve all descriptors for given project (both from main and realm specific contexts)'() {
        given:
            StubRepository repository = new StubRepository(REPOSITORY_LOCATION)
            ProjectMetadata projectMetadata = new ProjectMetadata('bye', 'com/ofg/bye', 'pl')
            List<MappingDescriptor> expectedDescriptors = [MAIN_BYE_STUB_DESCRIPTOR, ADMIN_STUB_DESCRIPTOR , PL_BYE_STUB_DESCRIPTOR, PL_OVERRIDDEN_BYE_STUB_DESCRIPTOR]
        when:
            List<MappingDescriptor> descriptors = repository.getProjectDescriptors(projectMetadata)
        then:
            descriptors.size() == expectedDescriptors.size()
            descriptors.containsAll(expectedDescriptors)
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
