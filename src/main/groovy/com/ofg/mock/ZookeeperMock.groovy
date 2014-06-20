package com.ofg.mock
import groovy.transform.TypeChecked
import org.apache.curator.test.TestingServer
import org.jboss.resteasy.test.NettyContainer

import javax.ws.rs.GET
import javax.ws.rs.Path

@TypeChecked
@Path("/")
class ZookeeperMock {
    static TestingServer testingServer

    @GET
    @Path("/stop")
    public void stop() {
        println "Stopping the zookeeper mock"
        testingServer.close()
        NettyContainer.stop()
    }

    public static void main(String[] args) {
        int port = 2181
        int mockPort = 18081

        if (args.size() > 2) {
            printUsage()
            return
        }
        if (args.size() >= 1) {
            if (!args[0].isInteger()) {
                printUsage()
                return
            }
            port = args[0].toInteger()
        }
        if (args.size() == 2) {
            if (!args[1].isInteger()) {
                printUsage()
                return
            }
            mockPort = args[1].toInteger()
        }
        System.setProperty("org.jboss.resteasy.port", "$mockPort")
        NettyContainer.start().getRegistry().addPerRequestResource(ZookeeperMock);
        testingServer = new TestingServer(port)
        println "ZooKeeper mock started..."
    }

    private static void printUsage() {
        println "USAGE:\n"
        println "\tjava -jar micro-deps-VERSION-fatJar.jar [portNumber, [mockPortNumber]] \n"
        println "\t\tWHERE\n"
        println "\t\tportNumber: optional port number on which zookeeper mock will be started. Default is 2181\n"
        println "\t\tmockPortNumber: optional port number on which zookeeper rest server will be started. " +
                "It will expose one method on /stop to stop the server. Default is 18081\n"
    }
}