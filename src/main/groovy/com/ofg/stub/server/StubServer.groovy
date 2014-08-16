package com.ofg.stub.server

import com.github.tomakehurst.wiremock.WireMockServer
import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@TypeChecked
@Slf4j
class StubServer {
    private static final MAPPING_ENDPOINT = '/__admin/mappings/new'

    private WireMockServer wireMockServer

    final int port
    final ProjectMetadata projectMetadata
    final Collection<MappingDescriptor> mappings

    StubServer(int port, ProjectMetadata projectMetadata, Collection<MappingDescriptor> mappings) {
        this.port = port
        this.projectMetadata = projectMetadata
        this.mappings = mappings
        wireMockServer = new WireMockServer(wireMockConfig().port(port))
    }

    void start() {
        wireMockServer.start()
        log.info("Started stub server for project $projectMetadata.projectName on port ${wireMockServer.port()}")
        registerStubMappings()
    }

    void stop() {
        wireMockServer.stop()
    }

    private void registerStubMappings() {
        // TODO: register with API call after https://github.com/tomakehurst/wiremock/pull/164 is merged
        HTTPBuilder httpBuilder = new HTTPBuilder("http://localhost:$port")
        mappings.each {
            httpBuilder.post(path: MAPPING_ENDPOINT, body: it.descriptor.text)
            log.debug("Registered stub mappings from $it.descriptor")
        }
    }
}
