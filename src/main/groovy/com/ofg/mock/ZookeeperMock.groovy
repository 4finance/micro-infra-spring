package com.ofg.mock

import groovy.transform.TypeChecked
import org.apache.curator.test.TestingServer

@TypeChecked
class ZookeeperMock {
    public static void main(String[] args) {
        int port = 2181

        if (args.size() > 1) {
            printUsage()
            return
        }

        if (args.size() == 1) {
            if (!args[0].isInteger()) {
                printUsage()
                return
            }

            port = args[0].toInteger()
        }

        TestingServer testingServer = new TestingServer(port)
    }

    static void printUsage() {
        println "USAGE:\n"
        println "\tjava -jar micro-deps-VERSION-fatJar.jar [portNumber]\n"
        println "\t\tWHERE\n"
        println "\t\tportNumber: optional port number on which zookeeper mock will be started. Default is 2181\n"
    }
}
