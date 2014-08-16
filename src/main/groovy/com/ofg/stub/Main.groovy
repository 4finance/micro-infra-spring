package com.ofg.stub

import com.ofg.stub.mapping.DescriptorRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

@TypeChecked
@Slf4j
class Main {

    static void main(String... args) {
        log.debug("Launching StubRunner with args: $args")
        if (args.size() < 5) {
            printUsage()
        }
        doMain(args)
    }

    private static void doMain(String... args) {
        File repositoryPath = new File(args[0])
        File metadata = new File(repositoryPath, args[1])
        StubRegistry stubRegistry = new StubRegistry(portNumber(args[2]))
        AvailablePortScanner portScanner = new AvailablePortScanner(portNumber(args[3]), portNumber(args[4]))
        DescriptorRepository repository = new DescriptorRepository(repositoryPath)
        StubRunner stubRunner = new StubRunner(portScanner, stubRegistry)
        stubRunner.runStubs(repository, metadata)
    }

    private static int portNumber(String port) {
        return Integer.parseInt(port)
    }

    private static printUsage() {
        println 'expected arguments: repositoryPath, metadataFile, stubRepositoryPort, minStubPortNumber, maxStubPortNumber'
    }
}
