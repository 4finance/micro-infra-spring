package com.ofg.stub

import com.google.common.base.Optional
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.mapping.ProjectMetadataResolver
import com.ofg.stub.mapping.StubRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.AvailablePortScanner
import com.ofg.stub.server.ZookeeperServer
import groovy.util.logging.Slf4j
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.ExplicitBooleanOptionHandler

import static org.apache.commons.lang.StringUtils.isNotBlank
import static org.kohsuke.args4j.OptionHandlerFilter.ALL

@Slf4j
class StubRunner implements StubRunning {

    private static final ThreadLocal<StubRunnerExecutor> stubRunner = new ThreadLocal<>()

    @Option(name = "-r", aliases = ['--repository'], usage = "Path to repository containing the 'repository' folder with 'project' and 'mapping' subfolders (e.g. '/home/4finance/stubs/')", required = true)
    private String repositoryPath

    @Option(name = "-p", aliases = ['--projectPath'], usage = "Relative path to the project which you want to run (e.g. '/com/ofg/foo/barProject.json')", forbids = ['-a'])
    private String projectRelativePath

    @Option(name = "-a", aliases = ['--runAllStubs'], usage = "Switch that signifies that you want to run all stubs (e.g. 'true')", forbids = ['-p'])
    private String runAllStubs

    @Option(name = "-z", aliases = ['--zookeeperPort'], usage = "Port of the zookeeper instance (e.g. 2181)", forbids = ['lz'])
    private Integer testingZookeeperPort = 2181

    @Option(name = "-zl", aliases = ['--zookeeperLocation'], usage = "Location of local Zookeeper you want to connect to (e.g. localhost:23456)", forbids = ['-z'])
    private String zookeeperLocation

    @Option(name = "-minp", aliases = ['--minPort'], usage = "Minimal port value to be assigned to the Wiremock instance (e.g. 12345)", required = true)
    private Integer minPortValue

    @Option(name = "-maxp", aliases = ['--maxPort'], usage = "Maximum port value to be assigned to the Wiremock instance (e.g. 12345)", required = true)
    private Integer maxPortValue

    @Option(name = "-c", aliases = ['--context'], usage = "Context for which the project should be run (e.g. 'pl', 'lt')", required = true)
    private String context

    @Option(name = "-n", aliases = ['--serviceName'], usage = "Name of the service for which the project should be run (e.g. 'com/service/name')")
    private String serviceName

    @Option(name = "-uz", aliases = ['--useZookeeperDepResolution'], usage = "Switch to use Zookeeper server to resolve dependencies of the service and run stubs for them", handler = ExplicitBooleanOptionHandler)
    private boolean useZookeeperDepResolution = true

    private final Arguments arguments
    private final StubRegistry stubRegistry
    private final StubRepository stubRepository
    private final ZookeeperServer zookeeperServer

    StubRunner(String[] args) {
        CmdLineParser parser = new CmdLineParser(this)
        try {
            parser.parseArgument(args)
            this.arguments = new Arguments(repositoryPath, projectRelativePath, testingZookeeperPort, minPortValue, maxPortValue, context, zookeeperLocation)
            this.zookeeperServer = resolveZookeeperServer()
            this.zookeeperServer.start()
            this.stubRegistry = new StubRegistry(zookeeperServer.connectString, zookeeperServer.curatorFramework)
            this.stubRepository = new StubRepository(new File(repositoryPath))
        } catch (CmdLineException e) {
            printErrorMessage(e, parser)
            throw e
        }
    }

    StubRunner(Arguments arguments, StubRegistry stubRegistry) {
        this.arguments = arguments
        this.stubRegistry = stubRegistry
        this.zookeeperServer = resolveZookeeperServer()
        this.stubRepository = new StubRepository(new File(arguments.repositoryPath))
    }

    private ZookeeperServer resolveZookeeperServer() {
        if (isNotBlank(arguments.localZookeeperPath)) {
            return new ZookeeperServer(arguments.localZookeeperPath)
        } else if (arguments.testingZookeeperPort) {
            return new ZookeeperServer(arguments.testingZookeeperPort)
        }
        throw new IllegalArgumentException('You have to provide either Zookeeper port or a path to a local Zookeeper')
    }

    private void printErrorMessage(CmdLineException e, CmdLineParser parser) {
        System.err.println(e.getMessage())
        System.err.println("java -jar stub-runner.jar [options...] ")
        parser.printUsage(System.err)
        System.err.println()
        System.err.println("Example: java -jar stub-runner.jar ${parser.printExample(ALL)}")
    }

    static void main(String[] args) {
        new StubRunner(args).execute()
    }

    private void execute() {
        log.debug("Launching StubRunner with args: $arguments")
        registerShutdownHook()
        runStubs()
    }

    private void registerShutdownHook() {
        Runnable stopAllServers = { this.close() }
        Runtime.runtime.addShutdownHook(new Thread(stopAllServers))
    }

    @Override
    void runStubs() {
        zookeeperServer.start()
        AvailablePortScanner portScanner = new AvailablePortScanner(arguments.minPortValue, arguments.maxPortValue)
        Collection<ProjectMetadata> projects = resolveProjects(stubRepository)
        StubRunnerExecutor localStubRunner = new StubRunnerExecutor(portScanner, stubRegistry)
        stubRunner.set(localStubRunner)
        localStubRunner.runStubs(stubRepository, projects)
    }

    @Override
    Optional<URL> findStubUrlByRelativePath(String relativePath) {
        return stubRunner.get().getStubUrlByRelativePath(relativePath)
    }

    private Collection<ProjectMetadata> resolveProjects(StubRepository repository) {
        if (arguments.projects) {
            return arguments.projects
        } else if (arguments.projectRelativePath) {
            File metadata = new File(repository.getProjectMetadataLocation(arguments.projectRelativePath))
            return ProjectMetadataResolver.resolveFromMetadata(metadata)
        } else if (useZookeeperDepResolution) {
            String name = serviceName ? serviceName : arguments.projectRelativePath
            String ctx = context ? context : arguments.context
            return ProjectMetadataResolver.resolveFromZookeeper(name, ctx, zookeeperServer)
        } else {
            return ProjectMetadataResolver.resolveAllProjectsFromRepository(repository, arguments.context)
        }
    }

    @Override
    void close() throws IOException {
        zookeeperServer.shutdown()
        stubRunner.get()?.shutdown()
    }
}
