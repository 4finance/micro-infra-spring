package com.ofg.stub

import com.ofg.stub.mapping.DescriptorRepository
import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import com.ofg.stub.server.StubServer
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@TypeChecked
@Slf4j
class StubRunner {

    private final AvailablePortScanner portScanner
    private final StubRegistry stubRegistry
    private final List<StubServer> stubServers = []

    StubRunner(AvailablePortScanner portScanner, StubRegistry stubRegistry) {
        this.portScanner = portScanner
        this.stubRegistry = stubRegistry
    }

    void runStubs(DescriptorRepository repository, List<ProjectMetadata> projects) {
        startStubServers(projects, repository)
        stubRegistry.register(stubServers)
        log.info("All stubs are now running and registered in service registry available on port $stubRegistry.port")
    }

    void shutdown() {
        stubRegistry.shutdown()
        stubServers.each { it.stop() }
    }

    private void startStubServers(List<ProjectMetadata> projects, DescriptorRepository repository) {
        projects.each {
            List<MappingDescriptor> mappings = repository.getAllProjectDescriptors(it)
            StubServer stubServer = new StubServer(portScanner.nextAvailablePort(), it, mappings)
            stubServer.start()
            stubServers << stubServer
        }
    }
}
