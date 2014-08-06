package com.ofg.mock

import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.kohsuke.args4j.Option

@TypeChecked
@PackageScope
class CmdOptions {

    private static final int DEFAULT_PORT_NUMBER = 2181

    private static final int DEFAULT_MOCK_PORT_NUMBER = 18081

    @Option(name = '-p', usage = "optional port number on which zookeeper mock will be started. Default is $DEFAULT_PORT_NUMBER")
    int portNumber = DEFAULT_PORT_NUMBER

    @Option(name = '-mp', usage = """optional port number on which rest control server will be started.
It will expose one method on /stop to stop the server. Default is $DEFAULT_MOCK_PORT_NUMBER""")
    int controlPortNumber = DEFAULT_MOCK_PORT_NUMBER

    @Option(name = '-c', usage = "json configuration with dependencies to load (exclusive with -f)", forbids = ['-f'], depends = ['-r'])
    String jsonConfig

    @Option(name = '-f', usage = "path to file with json config (exclusive with -c)", forbids = ['-c'], depends = ['-r'])
    String pathToConfig

    @Option(name = '-r', usage = "url to repository with stubs")
    String repository

}
