package com.ofg.stub

import com.ofg.stub.mapping.DescriptorRepository
import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.ProjectMetadataParser
import com.ofg.stub.mapping.RepositoryMetada
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import com.ofg.stub.server.StubServer
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import static com.ofg.stub.mapping.ProjectMetadataParser.createMetaDataForAllStubs
import static com.ofg.stub.mapping.ProjectMetadataParser.parseMetadata

@TypeChecked
@Slf4j
class StubRunner {

    private final AvailablePortScanner portScanner
    private final StubRegistry stubRegistry

    StubRunner(AvailablePortScanner portScanner, StubRegistry stubRegistry) {
        this.portScanner = portScanner
        this.stubRegistry = stubRegistry
    }

    void runStubs(DescriptorRepository repository, RepositoryMetada repositoryMetada) {
        List<ProjectMetadata> projects = repositoryMetada.isForAllStubs() ? createMetaDataForAllStubs(repositoryMetada) : parseMetadata(repositoryMetada)
        List<StubServer> stubServers = startStubServers(projects, repository)
        stubRegistry.register(stubServers)
        log.info("All stubs are now running and registered in service registry available on port $stubRegistry.port")
    }

    private List<StubServer> startStubServers(List<ProjectMetadata> projects, DescriptorRepository repository) {
        List<StubServer> stubServers = []
        projects.each {
            List<MappingDescriptor> mappings = repository.getAllProjectDescriptors(it)
            StubServer stubServer = new StubServer(portScanner.nextAvailablePort(), it, mappings)
            stubServer.start()
            stubServers << stubServer
        }
        return stubServers
    }
}
