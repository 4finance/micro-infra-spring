package com.ofg.stub.server

import org.apache.curator.test.TestingServer
import spock.lang.Specification
import spock.lang.Unroll

class AvailablePortScannerSpec extends Specification {

    private static final int MIN_PORT = 8989
    private static final int MAX_PORT = 8990

    def 'should execute given closure with the next available port number'() {
        given:
            AvailablePortScanner portScanner = new AvailablePortScanner(MIN_PORT, MAX_PORT)
        when:
            portScanner.tryToExecuteWithFreePort {}
        then:
            noExceptionThrown()
    }

    def 'should throw exception when free port number cannot be found'() {
        given:
            TestingServer server1 = new TestingServer(MIN_PORT, true)
            TestingServer server2 = new TestingServer(MAX_PORT, true)
        when:
            new AvailablePortScanner(MIN_PORT, MAX_PORT).tryToExecuteWithFreePort {}
        then:
            def ex = thrown(AvailablePortScanner.NoPortAvailableException)
            ex.message == "Could not find available port in range $MIN_PORT:$MAX_PORT"
        cleanup:
            server1.close()
            server2.close()
    }

    @Unroll("should throw exception for improper range [#minPort:#maxPort]")
    def 'should throw exception when improper range has been provided'() {
        given:
            AvailablePortScanner portScanner = new AvailablePortScanner(minPort, maxPort)
        when:
            portScanner.tryToExecuteWithFreePort {}
        then:
            def ex = thrown(AvailablePortScanner.NoPortAvailableException)
            ex.message == "Could not find available port in range $minPort:$maxPort"
        where:
            minPort  | maxPort
            MAX_PORT | MIN_PORT
            MIN_PORT | MIN_PORT
    }

}
