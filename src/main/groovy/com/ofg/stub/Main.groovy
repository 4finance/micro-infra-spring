package com.ofg.stub

import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.StubRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import static com.ofg.stub.mapping.ProjectMetadataResolver.resolveAllProjectsFromRepository
import static com.ofg.stub.mapping.ProjectMetadataResolver.resolveFromMetadata

@TypeChecked
@Slf4j
class Main {
    public static StubRunner stubRunner = null

    static void main(String[] args) {
        log.debug("Launching StubRunner with args: $args")
        if (args.size() < 5) {
            printUsage()
        }
        registerShutdownHook()
        runStubs(args)
    }

    private static void runStubs(String[] args) {
        File repositoryPath = new File(args[0])
        StubRepository repository = new StubRepository(repositoryPath)
        StubRegistry stubRegistry = new StubRegistry(portNumber(args[2]))
        AvailablePortScanner portScanner = new AvailablePortScanner(portNumber(args[3]), portNumber(args[4]))
        List<ProjectMetadata> projects = resolveProjects(repository, args)
        stubRunner = new StubRunner(portScanner, stubRegistry)
        stubRunner.runStubs(repository, projects)
    }

    private static void registerShutdownHook() {
        Runnable stopAllServers = { stubRunner?.shutdown() }
        Runtime.runtime.addShutdownHook(new Thread(stopAllServers))
    }

    private static List<ProjectMetadata> resolveProjects(StubRepository repository, String[] args) {
        if (args[1]) {
            File metadata = new File(repository.getProjectMetadataLocation(args[1]))
            return resolveFromMetadata(metadata)
        } else {
            return resolveAllProjectsFromRepository(repository, getContext(args))
        }
    }

    private static String getContext(String[] args) {
        args.length == 6 ? args[5] : null
    }

    private static int portNumber(String port) {
        return Integer.parseInt(port)
    }

    private static printUsage() {
        println 'expected arguments: repositoryPath, metadataFile, stubRepositoryPort, minStubPortNumber, maxStubPortNumber, context'
    }
}
