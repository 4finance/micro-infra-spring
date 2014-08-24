package com.ofg.stub

import com.ofg.stub.mapping.DescriptorRepository
import com.ofg.stub.mapping.RepositoryMetada
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@TypeChecked
@Slf4j
class Main {

    private static final String VALUE_MISSING = ''

    static void main(String... args) {
        log.debug("Launching StubRunner with args: $args")
        if (args.size() < 5) {
            printUsage()
        }
        doMain(args)
    }

    private static void doMain(String... args) {
        File repositoryPath = new File(args[0])
        File metadata = args[1] ? new File(repositoryPath, args[1]) : null
        StubRegistry stubRegistry = new StubRegistry(portNumber(args[2]))
        AvailablePortScanner portScanner = new AvailablePortScanner(portNumber(args[3]), portNumber(args[4]))
        RepositoryMetada repositoryMetada = new RepositoryMetada(repositoryPath, metadata, parseContext(args))
        DescriptorRepository repository = new DescriptorRepository(repositoryPath)
        StubRunner stubRunner = new StubRunner(portScanner, stubRegistry)
        stubRunner.runStubs(repository, repositoryMetada)
    }

    private static String parseContext(String... args) {
        args.length == 6 ? args[5] : VALUE_MISSING
    }

    private static int portNumber(String port) {
        return Integer.parseInt(port)
    }

    private static printUsage() {
        println 'expected arguments: repositoryPath, metadataFile, stubRepositoryPort, minStubPortNumber, maxStubPortNumber, context'
    }
}
