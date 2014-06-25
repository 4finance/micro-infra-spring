package com.ofg.infrastructure.base
import groovy.transform.TypeChecked
import org.apache.curator.test.TestingServer
import spock.lang.Specification

@TypeChecked
class ZookeeperSpec extends Specification {

    TestingServer server
    
    def setup() {
        server = new TestingServer();
    }
    
    def cleanup() {
        server.close()
    }
    
}
