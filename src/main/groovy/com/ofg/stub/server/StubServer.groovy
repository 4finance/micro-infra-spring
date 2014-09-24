package com.ofg.stub.server
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@CompileStatic
@Slf4j
class StubServer {
    private static final MAPPING_ENDPOINT = '/__admin/mappings/new'

    private WireMockServer wireMockServer

    final ProjectMetadata projectMetadata
    final Collection<MappingDescriptor> mappings

    StubServer(int port, ProjectMetadata projectMetadata, Collection<MappingDescriptor> mappings) {
        this.projectMetadata = projectMetadata
        this.mappings = mappings
        wireMockServer = new WireMockServer(wireMockConfig().port(port))
    }

    void start() {
        wireMockServer.start()
        log.info("Started stub server for project $projectMetadata.projectRelativePath on port ${wireMockServer.port()}")
        registerStubMappings()
    }

    void stop() {
        wireMockServer.stop()
    }

    int getPort() {
        return wireMockServer.port()
    }

    private void registerStubMappings() {
        WireMock wireMock = new WireMock('localhost', wireMockServer.port())
        List<MappingDescriptor> sortedMappings =  mappings.sort(byGlobalMappingsFirst())
        registerStubs(sortedMappings, wireMock)
    }

    private Iterable<MappingDescriptor> registerStubs(List<MappingDescriptor> sortedMappings, WireMock wireMock) {
        sortedMappings.each {
            wireMock.register(it.mapping)
            log.debug("Registered stub mappings from $it.descriptor")
        }
    }

    private Closure byGlobalMappingsFirst() {
        return {
            MappingDescriptor first, MappingDescriptor other ->
                first.mappingType == MappingDescriptor.MappingType.GLOBAL ? -1 : 1
        }
    }
}
