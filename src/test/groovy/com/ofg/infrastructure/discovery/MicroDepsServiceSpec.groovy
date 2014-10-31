package com.ofg.infrastructure.discovery
import com.jayway.awaitility.groovy.AwaitilitySupport
import com.ofg.infrastructure.discovery.util.MicroDepsService
import com.ofg.infrastructure.discovery.watcher.DependencyState
import com.ofg.infrastructure.discovery.watcher.DependencyWatcherListener
import org.apache.curator.test.TestingServer
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@Mixin(AwaitilitySupport)
class MicroDepsServiceSpec extends Specification {

    TestingServer server

    final static String MICRO_A = """
{
    "test": {
        "this": "com/test/microA",
        "dependencies": [
        ]
    }
}
    """

    final static String MICRO_B = """
{
    "test": {
        "this": "com/test/microB",
        "dependencies": [{
            "name" : "microA",
            "path": "com/test/microA"
        }]
    }
}
    """

    def setup() {
        setupTestingServer()
    }

    private void setupTestingServer() {
        server = new TestingServer()
    }

    def "should setup service discovery properly"() {
        given:
        def microAService =
                new MicroDepsService(server.connectString, "rest", "microAUrl", 8877, MICRO_A)

        def microBService =
                new MicroDepsService(server.connectString, "rest", "microBUrl", 8888,  MICRO_B)

        String dep = null
        DependencyState state = null

        DependencyWatcherListener listener = new DependencyWatcherListener() {
            @Override
            void stateChanged(String dependencyName, DependencyState newState) {
                dep = dependencyName
                state = newState
            }
        }

        when:
        microAService.start()

        microBService.registerDependencyStateChangeListener(listener)
        microBService.start()

        microAService.stop()

        await().atMost(5, TimeUnit.SECONDS).until {
            dep == "microA" && state == DependencyState.DISCONNECTED
        }

        then:
        noExceptionThrown()

        when:
        microAService = new MicroDepsService(server.connectString, "rest", "microAUrl", 8877, MICRO_A)
        microAService.start()

        await().atMost(5, TimeUnit.SECONDS).until {
            dep == "microA" && state == DependencyState.CONNECTED
        }

        then:
        noExceptionThrown()

        cleanup:
        microBService?.stop()
        microAService?.stop()
    }

    def cleanup() {
        server.close()
    }
}
