package com.ofg.stub.server

import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.StubRepository
import spock.lang.Specification

class StubServerSpec extends Specification {
    static final int STUB_SERVER_PORT = 12180

    File repository = new File('src/test/resources/repository')
    ProjectMetadata projectMetadata = new ProjectMetadata('bye', 'com/ofg/bye', 'pl')

    def 'should register stub mappings upon server start'() {
        given:
            List<MappingDescriptor> mappingDescriptors = new StubRepository(repository).getProjectDescriptors(projectMetadata)
            StubServer pingStubServer = new StubServer(STUB_SERVER_PORT, projectMetadata, mappingDescriptors)
        when:
            pingStubServer.start()
        then:
            "http://localhost:$pingStubServer.port/pl/bye".toURL().text == 'pl-bye'
            "http://localhost:$pingStubServer.port/bye".toURL().text == 'overridden-bye'
        cleanup:
            pingStubServer.stop()
    }
}
