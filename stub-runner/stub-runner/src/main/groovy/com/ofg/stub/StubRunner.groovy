package com.ofg.stub

import com.google.common.base.Optional
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.StubRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import com.ofg.stub.server.ZookeeperServer
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import static org.apache.commons.lang.StringUtils.isNotBlank

/**
 * Core class that executes the StubRunner functionality
 */
@Slf4j
@CompileStatic
class StubRunner implements StubRunning {

    private StubRunnerExecutor localStubRunner
    private final Arguments arguments
    private final StubRegistry stubRegistry
    private final StubRepository stubRepository
    private final ZookeeperServer zookeeperServer

    StubRunner(Arguments arguments, StubRegistry stubRegistry) {
        this.arguments = arguments
        this.stubRegistry = stubRegistry
        this.zookeeperServer = resolveZookeeperServer()
        this.stubRepository = new StubRepository(new File(arguments.repositoryPath))
    }

    private ZookeeperServer resolveZookeeperServer() {
        if (isNotBlank(arguments.stubRunnerOptions.zookeeperConnectString)) {
            return new ZookeeperServer(arguments.stubRunnerOptions.zookeeperConnectString)
        } else if (arguments.stubRunnerOptions.zookeeperPort) {
            return new ZookeeperServer(arguments.stubRunnerOptions.zookeeperPort)
        }
        throw new IllegalArgumentException('You have to provide either Zookeeper port or a path to a local Zookeeper')
    }

    @Override
    void runStubs() {
        zookeeperServer.start()
        AvailablePortScanner portScanner = new AvailablePortScanner(arguments.stubRunnerOptions.minPortValue, arguments.stubRunnerOptions.maxPortValue)
        Collection<ProjectMetadata> projects = arguments.projects
        localStubRunner = new StubRunnerExecutor(portScanner, stubRegistry)
        registerShutdownHook()
        localStubRunner.runStubs(stubRepository, projects)
    }

    @Override
    Optional<URL> findStubUrlByRelativePath(String relativePath) {
        return localStubRunner.getStubUrlByRelativePath(relativePath)
    }

    private void registerShutdownHook() {
        Runnable stopAllServers = { this.close() }
        Runtime.runtime.addShutdownHook(new Thread(stopAllServers))
    }

    @Override
    void close() throws IOException {
        zookeeperServer.shutdown()
        localStubRunner?.shutdown()
    }
}