package com.ofg.stub.mapping

import spock.lang.Specification

class DescriptorRepositorySpec extends Specification {
    static final File REPOSITORY_LOCATION = new File('src/test/resources/repository')
    static final MappingDescriptor HELLO_STUB_DESCRIPTOR = new MappingDescriptor(new File(REPOSITORY_LOCATION, 'com/ofg/hello/hello.json'))
    static final MappingDescriptor ADMIN_STUB_DESCRIPTOR = new MappingDescriptor(new File(REPOSITORY_LOCATION, 'com/ofg/hello/admin/admin.json'))

    DescriptorRepository repository = new DescriptorRepository(REPOSITORY_LOCATION)

    def 'should retrieve all descriptors for given project'() {
        given:
            ProjectMetadata projectMetadata = new ProjectMetadata('com/ofg/hello', 'pl')

        when:
            List<MappingDescriptor> descriptors = repository.getAllProjectDescriptors(projectMetadata)

        then:
            descriptors.size() == 2
            descriptors.contains(HELLO_STUB_DESCRIPTOR)
            descriptors.contains(ADMIN_STUB_DESCRIPTOR)
    }
}
