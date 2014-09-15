package com.ofg.stub.server

import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import spock.lang.AutoCleanup
import spock.lang.Specification

class StubServerSpec extends Specification {
    static final int STUB_SERVER_PORT = 12180

    MappingDescriptor mappingDescriptor = new MappingDescriptor(new File('src/test/resources/repository/com/ofg/ping/ping.json'))
    ProjectMetadata projectMetadata = new ProjectMetadata('com/ofg/ping', 'pl')

    @AutoCleanup('stop') StubServer pingStubServer = new StubServer(STUB_SERVER_PORT, projectMetadata, [mappingDescriptor])

    def 'should register stub mappings upon server start'() {
        when:
            pingStubServer.start()

        then:
            "http://localhost:$pingStubServer.port/ping".toURL().text == 'pong'
    }
}
