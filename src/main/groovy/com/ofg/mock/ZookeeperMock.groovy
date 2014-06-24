package com.ofg.mock
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import groovy.transform.TypeChecked
import org.apache.curator.test.TestingServer
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option

import javax.ws.rs.ApplicationPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Application

@TypeChecked
@Path("/")
class ZookeeperMock {
    static TestingServer testingServer

    private static final int DEFAULT_PORT_NUMBER = 2181
    
    private static final int DEFAULT_MOCK_PORT_NUMBER = 18081
    
    @Option(name = '-p', usage = "optional port number on which zookeeper mock will be started. Default is 2181")
    private int portNumber = DEFAULT_PORT_NUMBER
    
    @Option(name = '-mp', usage = """optional port number on which zookeeper rest server will be started.
It will expose one method on /stop to stop the server. Default is 18081""")
    private int controlPortNumber = DEFAULT_MOCK_PORT_NUMBER 
    
    @Option(name = '-c', usage = "json configuration with dependencies to load (exclusive with -f)", forbids = ['-f'])
    private String jsonConfig
    
    @Option(name = '-f', usage = "path to file with json config (exclusive with -c)", forbids = ['-c'])
    private String pathToConfig
    
    @Option(name = '-r', usage = "url to repository with stubs")
    private String repository
    
    private static UndertowJaxrsServer server
    
    @GET
    @Path("/stop")
    public String stop() {
        try {
            println "Stopping the zookeeper mock"
            testingServer.close()
            scheduleShutdownIn1Second()
            return "Zookeeper mock stopped"
        } catch (Exception e) {
            return e.toString()
        }
    }

    private static void scheduleShutdownIn1Second() {
        new Thread({
            Thread.sleep(1000)
            shutdown()
        }).start()
    }

    private static void shutdown() {
        server.stop()
        println "rest stopped"
        System.exit(-1)
    }
    
    public static void main(String[] args) {
        try {
            new ZookeeperMock().doMain(args)
        } catch (Exception e) {
            e.printStackTrace()
            shutdown()
        }
    }
    
    private void doMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            startZookeeperMock()
            startStubs()
        } catch( CmdLineException e ) {
            System.err.println(e.getMessage())
            parser.printUsage(System.err)
        }
    }

    private void startStubs() {
        if (jsonConfig || pathToConfig) {
            println "Starting stubs"
            ServiceConfigurationResolver resolver = new ServiceConfigurationResolver(jsonConfig ?: new File(pathToConfig).text)
            if (!repository) {
                throw new CmdLineException('In order to load stubs you must provide a repository with -r')
            }
            new StubLoader().loadStubs(resolver, repository, portNumber)
        }
    }

    private void startZookeeperMock() {
        System.setProperty("org.jboss.resteasy.port", "$controlPortNumber")
        server = new UndertowJaxrsServer().start()
        server.deploy(RestApp)
        testingServer = new TestingServer(portNumber)
        println "ZooKeeper mock started with mock controll port [$controlPortNumber] and zookeeper port [$portNumber]"
    }

    @ApplicationPath("/")
    static class RestApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return [ZookeeperMock] as Set
        }
    }
}