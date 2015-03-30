package com.ofg.stub

import com.google.common.base.Optional
import com.ofg.stub.mapping.MappingDescriptor
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.StubRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import com.ofg.stub.server.StubServer
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
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
        log.info("All stubs are now running and registered in service registry available at [$stubRegistry.connectString]")
    }

    void shutdown() {
        stubServers.each { StubServer stubServer -> stubServer.stop() }
    }

    /**
     * Finds {@link URL} to a stubbed dependency by given {@code dependencyPath}.
     *
     * @param dependencyPath path taken from microservice configuration from dependency settings
     *
     * @return {@link URL} to stubbed dependency wrapped if found otherwise {@link Optional#absent()}
     */
    Optional<URL> getStubUrlByRelativePath(String dependencyPath) {
        return Optional.fromNullable(stubServers.find {
            return it.projectMetadata.projectRelativePath == dependencyPath
        }?.stubUrl)
    }

    private void startStubServers(Collection<ProjectMetadata> projects, StubRepository repository) {
        stubServers.addAll(projects.collect { ProjectMetadata projectMetadata ->
            List<MappingDescriptor> mappings = repository.getProjectDescriptors(projectMetadata)
            return portScanner.tryToExecuteWithFreePort { int availablePort ->
                return new StubServer(availablePort, projectMetadata, mappings).start()
            }
        })
    }
}
