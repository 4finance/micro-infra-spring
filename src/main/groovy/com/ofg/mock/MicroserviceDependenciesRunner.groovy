package com.ofg.mock
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import groovy.transform.TypeChecked
import org.apache.curator.test.TestingServer
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser

import javax.ws.rs.ApplicationPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Application

@TypeChecked
@Path("/")
class MicroserviceDependenciesRunner {

    private final CmdOptions options

    private UndertowJaxrsServer controlServer

    private TestingServer zookeeperTestingServer

    private StubLoader stubLoader

    public static void main(String[] args) {
        CmdOptions options = parseArgs(args)
        MicroserviceDependenciesRunner runner = new MicroserviceDependenciesRunner(options)
        try {
            runner.doMain()
        } catch (Exception e) {
            e.printStackTrace()
            runner.shutdownOnFailure()
        }
    }

    private static CmdOptions parseArgs(String[] args) {
        CmdOptions options = new CmdOptions()
        CmdLineParser parser = new CmdLineParser(options)
        try {
            parser.parseArgument(args);
            return options
        } catch (CmdLineException e) {
            System.err.println(e.getMessage())
            parser.printUsage(System.err)
            throw e;
        }
    }

    public MicroserviceDependenciesRunner(final CmdOptions options) {
        this.options = options
    }

    private void doMain() {
        startZookeeperMock()
        if (stubLoadingEnabled) {
            startStubs()
        }
    }

    private void startZookeeperMock() {
        System.setProperty("org.jboss.resteasy.port", "$options.controlPortNumber")
        controlServer = new UndertowJaxrsServer().start()
        controlServer.deploy(RestApp)
        zookeeperTestingServer = new TestingServer(options.portNumber)
        println "ZooKeeper mock started with mock control port [$options.controlPortNumber] and zookeeper port [$options.portNumber]"
    }

    private boolean isStubLoadingEnabled() {
        return options.jsonConfig || options.pathToConfig
    }

    private void startStubs() {
        println "Starting stubs"
        stubLoader = new StubLoader()
        ServiceConfigurationResolver resolver = new ServiceConfigurationResolver(options.jsonConfig ?: new File(options.pathToConfig).text)
        stubLoader.loadStubs(resolver, options.repository, options.portNumber)
    }

    @GET
    @Path("/stop")
    public String stop() {
        try {
            println "Stopping the zookeeper mock"
            stubLoader.unloadStubs()
            scheduleShutdownIn1Second()
            return "Zookeeper mock stopped"
        } catch (Exception e) {
            return e.toString()
        }
    }

    private void shutdownOnFailure() {
        zookeeperTestingServer.close()
        controlServer.stop()
        println "rest stopped"
        System.exit(-1)
    }

    /**
     * Added to let rest control server properly respond to request on /stop endpoint.
     */
    private void scheduleShutdownIn1Second() {
        new Thread({
            Thread.sleep(1000)
            shutdownOnFailure()
        }).start()
    }

    @ApplicationPath("/")
    static class RestApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return [MicroserviceDependenciesRunner] as Set
        }
    }
}