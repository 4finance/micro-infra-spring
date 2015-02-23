package com.ofg.stub

import com.ofg.stub.mapping.StubRepository
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.server.ZookeeperServer
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option

import static org.apache.commons.lang.StringUtils.isNotBlank
import static org.kohsuke.args4j.OptionHandlerFilter.ALL

/**
 * Class having the main method to be executed in the fatJar
 */
@Slf4j
@CompileStatic
class StubRunnerMain {

    @Deprecated
    @Option(name = "-r", aliases = ['--repository'], usage = "@Deprecated - Path to repository containing the 'repository' folder with 'project' and 'mapping' subfolders (e.g. '/home/4finance/stubs/')", forbids = ['-n'])
    private String repositoryPath

    @Option(name = "-z", aliases = ['--zookeeperPort'], usage = "Port of the zookeeper instance (e.g. 2181)", forbids = ['lz'])
    private Integer testingZookeeperPort

    @Option(name = "-zl", aliases = ['--zookeeperLocation'], usage = "Location of local Zookeeper you want to connect to (e.g. localhost:23456)", forbids = ['-z'])
    private String zookeeperLocation

    @Option(name = "-sr", aliases = ['--stubRepositoryRoot'], usage = "Location of a Jar containing server where you keep your stubs (e.g. http://nexus.4finance.net/content/repositories/Pipeline)")
    private String stubRepositoryRoot

    @Deprecated
    @Option(name = "-sg", aliases = ['--stubsGroup'], usage = "@Deprecated - Name of the group where you store your stub definitions (e.g. com.ofg)")
    private String stubsGroup

    @Deprecated
    @Option(name = "-sm", aliases = ['--stubsModule'], usage = "@Deprecated - Name of the module where you store your stub definitions (e.g. stub-definitions)")
    private String stubsModule

    @Option(name = "-ss", aliases = ['--stubsSuffix'], usage = "Suffix for the jar containing stubs (e.g. 'stubs' if the stub jar would have a 'stubs' classifier for stubs: foobar-stubs ). Defaults to 'stubs'")
    private String stubsSuffix = 'stubs'

    @Option(name = "-minp", aliases = ['--minPort'], usage = "Minimal port value to be assigned to the Wiremock instance (e.g. 12345)", required = true)
    private Integer minPortValue

    @Option(name = "-maxp", aliases = ['--maxPort'], usage = "Maximum port value to be assigned to the Wiremock instance (e.g. 12345)", required = true)
    private Integer maxPortValue

    @Option(name = "-s", aliases = ['--skipLocalRepo'], usage = "Switch to check whether local repository check should be skipped and dependencies should be grabbed directly from the net. Defaults to 'false'")
    private Boolean skipLocalRepo

    @Option(name = "-md", aliases = ['--useMicroserviceDefinitions'], usage = "Switch to define whether you want to use the new approach with microservice definitions. Defaults to 'true'. To use old version switch to 'false'")
    private Boolean useMicroserviceDefinitions = true

    @Option(name = "-c", aliases = ['--context'], usage = "Context for which the project should be run (e.g. 'pl', 'lt')", required = true)
    private String context

    @Option(name = "-n", aliases = ['--serviceName'], usage = "Name of the service under which it is registered in Zookeeper. (e.g. 'com/service/name')", forbids = ['-r'])
    private String serviceName

    private final Arguments arguments
    private final StubRegistry stubRegistry
    private final StubRepository stubRepository
    private final ZookeeperServer zookeeperServer

    StubRunnerMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this)
        try {
            parser.parseArgument(args)
            this.arguments = new Arguments(new StubRunnerOptions(minPortValue, maxPortValue, stubRepositoryRoot,
                    stubsGroup, stubsModule, skipLocalRepo, useMicroserviceDefinitions, zookeeperLocation,
                    testingZookeeperPort, stubsSuffix),
                    context, repositoryPath, serviceName)
            this.zookeeperServer = resolveZookeeperServer()
            this.zookeeperServer.start()
            this.stubRegistry = new StubRegistry(zookeeperServer.connectString, zookeeperServer.curatorFramework)
            this.stubRepository = repositoryPath != null ? new StubRepository(new File(repositoryPath)) : null
        } catch (CmdLineException e) {
            printErrorMessage(e, parser)
            throw e
        }
    }

    private ZookeeperServer resolveZookeeperServer() {
        if (isNotBlank(arguments.stubRunnerOptions.zookeeperConnectString)) {
            return new ZookeeperServer(arguments.stubRunnerOptions.zookeeperConnectString)
        } else if (arguments.stubRunnerOptions.zookeeperPort) {
            return new ZookeeperServer(arguments.stubRunnerOptions.zookeeperPort)
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
        new StubRunnerMain(args).execute()
    }

    private void execute() {
        log.debug("Launching StubRunner with args: $arguments")
        Collaborators collaborators = CollaboratorsPathResolver.resolveFromZookeeper(arguments.serviceName, arguments.context, zookeeperServer)
        BatchStubRunner stubRunner = new BatchStubRunnerFactory(arguments.stubRunnerOptions, collaborators).buildBatchStubRunner()
        stubRunner.runStubs()
    }

}