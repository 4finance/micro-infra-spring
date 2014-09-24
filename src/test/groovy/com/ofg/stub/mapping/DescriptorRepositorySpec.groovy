package com.ofg.stub.mapping

import spock.lang.Specification

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

class DescriptorRepositorySpec extends Specification {
    static final File REPOSITORY_LOCATION = new File('src/test/resources/repository')
    static final MappingDescriptor MAIN_BYE_STUB_DESCRIPTOR = new MappingDescriptor(new File(REPOSITORY_LOCATION, 'com/ofg/bye/bye.json'))
    static final MappingDescriptor ADMIN_STUB_DESCRIPTOR = new MappingDescriptor(new File(REPOSITORY_LOCATION, 'com/ofg/bye/admin/admin.json'))
    static final MappingDescriptor PL_BYE_STUB_DESCRIPTOR = new MappingDescriptor(new File(REPOSITORY_LOCATION, 'pl/com/ofg/bye/pl_bye.json'), REALM_SPECIFIC)
    static final MappingDescriptor PL_OVERRIDDEN_BYE_STUB_DESCRIPTOR = new MappingDescriptor(new File(REPOSITORY_LOCATION, 'pl/com/ofg/bye/pl_overridden_bye.json'), REALM_SPECIFIC)

    DescriptorRepository repository = new DescriptorRepository(REPOSITORY_LOCATION)

    def 'should retrieve all descriptors for given project (both from main and realm specific contexts)'() {
        given:
            ProjectMetadata projectMetadata = new ProjectMetadata('com/ofg/bye', 'pl')
            List<MappingDescriptor> expectedDescriptors = [MAIN_BYE_STUB_DESCRIPTOR, ADMIN_STUB_DESCRIPTOR , PL_BYE_STUB_DESCRIPTOR, PL_OVERRIDDEN_BYE_STUB_DESCRIPTOR]
        when:
            List<MappingDescriptor> descriptors = repository.getAllProjectDescriptors(projectMetadata)

        then:
            descriptors.size() == expectedDescriptors.size()
            descriptors.containsAll(expectedDescriptors)
    }
}
