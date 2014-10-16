package com.ofg.stub

import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.StubRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import com.ofg.stub.server.StubServer
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@TypeChecked
@Slf4j
class StubRunnerExecutor {

    private final AvailablePortScanner portScanner
    private final StubRegistry stubRegistry
    private final List<StubServer> stubServers = []

    StubRunnerExecutor(AvailablePortScanner portScanner, StubRegistry stubRegistry) {
        this.portScanner = portScanner
        this.stubRegistry = stubRegistry
    }

    void runStubs(StubRepository repository, Collection<ProjectMetadata> projects) {
        startStubServers(projects, repository)
        stubRegistry.register(stubServers)
        log.info("All stubs are now running and registered in service registry available on port $stubRegistry.port")
    }

    void shutdown() {
        stubRegistry.shutdown()
        stubServers.each { StubServer stubServer -> stubServer.stop() }
    }

    private void startStubServers(Collection<ProjectMetadata> projects, StubRepository repository) {
        projects.each { ProjectMetadata projectMetadata ->
            List<MappingDescriptor> mappings = repository.getProjectDescriptors(projectMetadata)
            StubServer stubServer = new StubServer(portScanner.nextAvailablePort(), projectMetadata, mappings)
            stubServer.start()
            stubServers << stubServer
        }
    }
}
