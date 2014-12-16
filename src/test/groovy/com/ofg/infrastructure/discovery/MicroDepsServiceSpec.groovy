package com.ofg.infrastructure.discovery
import com.jayway.awaitility.groovy.AwaitilitySupport
import com.ofg.infrastructure.discovery.util.MicroDepsService
import com.ofg.infrastructure.discovery.watcher.DependencyState
import com.ofg.infrastructure.discovery.watcher.DependencyWatcherListener
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceProvider
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@Mixin(AwaitilitySupport)
class MicroDepsServiceSpec extends Specification {

    private static final RetryPolicy RETRY_POLICY = new RetryNTimes(50, 100)
    private TestingServer server
    private CuratorFramework curatorFramework

    private final static String MICRO_A = """
                                            {
                                                "test": {
                                                    "this": "com/test/microA"
                                                }
                                            }
                                            """

    private final static String MICRO_B = """
                                    {
                                        "test": {
                                            "this": "com/test/microB",
                                            "dependencies": {
                                                "microA" : {
                                                    "path": "com/test/microA"
                                                }
                                            }
                                        }
                                    }
                                    """

    def setup() {
        setupTestingServer()
    }

    private void setupTestingServer() {
        server = new TestingServer()
        curatorFramework = CuratorFrameworkFactory.newClient(server.connectString, RETRY_POLICY)
        curatorFramework.start()
    }

    def 'should register dependencies of a service as payload'() {
        given:
            MicroDepsService testService =
                    new MicroDepsService(server.connectString, "pl", "microUrl", 8866, MicroserviceConfiguration.FLAT_CONFIGURATION)
            testService.start()
        when:
            ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(InstanceDetails)
                    .basePath('/pl')
                    .client(curatorFramework)
                    .build()
            discovery.start()
            ServiceProvider serviceProvider = discovery.serviceProviderBuilder().serviceName('com/ofg/service').build()
            serviceProvider.start()
            InstanceDetails payload = serviceProvider.getInstance().payload
        then:
            payload.dependencies == ['com/ofg/ping', 'com/ofg/pong']
        cleanup:
            serviceProvider?.close()
            discovery?.close()
            testService?.stop()
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
        curatorFramework.close()
        server.close()
    }
}
