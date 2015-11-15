package com.ofg.stub.server

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static com.ofg.stub.mapping.MappingDescriptor.MappingType.GLOBAL

@CompileStatic
@Slf4j
class StubServer {
    private WireMockServer wireMockServer

    final ProjectMetadata projectMetadata
    final Collection<MappingDescriptor> mappings

    StubServer(int port, ProjectMetadata projectMetadata, Collection<MappingDescriptor> mappings) {
        this.projectMetadata = projectMetadata
        this.mappings = mappings
        wireMockServer = new WireMockServer(wireMockConfig().port(port))
    }

    StubServer start() {
        wireMockServer.start()
        log.info("Started stub server for project $projectMetadata.projectRelativePath on port ${wireMockServer.port()}")
        registerStubMappings()
        return this
    }

    StubServer stop() {
        wireMockServer.stop()
        return this
    }

    int getPort() {
        return wireMockServer.port()
    }

    URL getStubUrl() {
        return new URL("http://localhost:$port")
    }

    private void registerStubMappings() {
        WireMock wireMock = new WireMock('localhost', wireMockServer.port())
        List<MappingDescriptor> sortedMappings =  mappings.sort(byGlobalMappingsFirst())
        registerDefaultHealthChecks(wireMock)
        registerStubs(sortedMappings, wireMock)
    }

    private void registerDefaultHealthChecks(WireMock wireMock) {
        registerHealthCheck(wireMock, '/ping')
        registerHealthCheck(wireMock, '/health')
        registerCollabs(wireMock)
    }

    private void registerCollabs(WireMock wireMock) {
        wireMock.register(WireMock.get(WireMock.urlEqualTo('/collaborators')).willReturn(WireMock.aResponse().withHeader('Content-Type', 'application/json').withBody('{}').withStatus(200)))
    }

    private void registerStubs(List<MappingDescriptor> sortedMappings, WireMock wireMock) {
        sortedMappings.each { MappingDescriptor mappingDescriptor ->
            wireMock.register(mappingDescriptor.mapping)
            log.debug("Registered stub mappings from $mappingDescriptor.descriptor")
        }
    }

    void registerHealthCheck(WireMock wireMock, String url, String body = 'OK') {
        wireMock.register(WireMock.get(WireMock.urlEqualTo(url)).willReturn(WireMock.aResponse().withBody(body).withStatus(200)))
    }

    private static Closure byGlobalMappingsFirst() {
        return {
            MappingDescriptor first, MappingDescriptor other ->
                first.mappingType == GLOBAL ? -1 : 1
        }
    }
}
