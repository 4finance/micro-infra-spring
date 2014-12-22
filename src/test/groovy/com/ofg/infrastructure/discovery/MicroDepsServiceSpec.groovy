package com.ofg.infrastructure.discovery

import com.google.common.base.Optional
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
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@Mixin(AwaitilitySupport)
class MicroDepsServiceSpec extends Specification {

    private static final RetryPolicy RETRY_POLICY = new RetryNTimes(50, 100)
    @AutoCleanup('close')
    TestingServer server
    private CuratorFramework curatorFramework

    private static final String MICRO_A = """
{
    "uk": {
        "this": "com/test/microA"
    }
}
    """

    private static final String MICRO_B = """
{
    "uk": {
        "this": "com/test/microB",
        "dependencies": {
            "microA" : {
                "path": "com/test/microA"
            }
        }
    }
}
    """

    private final static String MICRO_C = """ {
    "uk": {
        "this": "com/test/special/microC"
    }
} """

    private final static String MICRO_D = """ {
    "uk": {
        "this": "org/dee/microD"
    }
} """

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
            CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(server.connectString, RETRY_POLICY)
            curatorFramework.start()
            ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(InstanceDetails)
                    .basePath('/pl')
                    .client(curatorFramework)
                    .build()
            discovery.start()
            ServiceProvider serviceProvider = discovery.serviceProviderBuilder().serviceName('com/ofg/service').build()
            serviceProvider.start()
            InstanceDetails payload = serviceProvider.instance.payload
        then:
            payload.dependencies.size() == 2
            payload.dependencies.contains('com/ofg/pong')
            payload.dependencies.contains('com/ofg/ping')
        cleanup:
            serviceProvider?.close()
            discovery?.close()
            testService?.stop()
            curatorFramework?.close()
    }

    def "should setup service discovery properly"() {
        given:
            def microAService =
                    new MicroDepsService(server.connectString, "rest", "microAUrl", 8877, MICRO_A)

            def microBService =
                    new MicroDepsService(server.connectString, "rest", "microBUrl", 8888, MICRO_B)

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

    def 'should enumerate names of all microservices'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "rest", "microAUrl", 8877, MICRO_A)
            service.start()
            new MicroDepsService(server.connectString, "rest", "microBUrl", 8888, MICRO_B).start()
            new MicroDepsService(server.connectString, "rest", "microCUrl", 8899, MICRO_C).start()
            new MicroDepsService(server.connectString, "rest", "microDUrl", 8800, MICRO_D).start()

        when:
            Set<ServicePath> names = service.serviceResolver.fetchAllDependencies()

        then:
            names*.path.toSet() == ['com/test/microA', 'com/test/microB', 'com/test/special/microC', 'org/dee/microD'].toSet()
    }

    def 'should enumerate all instances of a microservices'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "api", "micro-a", 8877, MICRO_A)
            service.start()
            new MicroDepsService(server.connectString, "api", "micro-a", 8878, MICRO_A).start()

        when:
            Set<URI> uris = service.serviceResolver.fetchAllUris(new ServicePath('com/test/microA'))

        then:
            uris == ['http://micro-a:8877/api', 'http://micro-a:8878/api']*.toURI().toSet()
    }

    def 'should resolve alias to full path'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "rest", "microBUrl", 8877, MICRO_B)
            service.start()

        when:
            ServicePath path = service.serviceResolver.resolveAlias(new ServiceAlias('microA'))

        then:
            path.path == 'com/test/microA'
    }

    def 'should fail to resolve not our dependency'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "rest", "microBUrl", 8877, MICRO_B)
            service.start()

        when:
            service.serviceResolver.resolveAlias(new ServiceAlias('microXXX'))

        then:
            NoSuchElementException e = thrown(NoSuchElementException)
            e.message.contains('microXXX')
            e.message.contains('microA')
    }

    def 'should return single available URI for given service path'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "api", "micro-b", 8877, MICRO_B)
            service.start()

        when:
            Optional<URI> uri = service.serviceResolver.getUri(new ServicePath('com/test/microB'))

        then:
            uri == Optional.of('http://micro-b:8877/api'.toURI())
    }

    def 'should always return single available URI for given service path'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "api", "micro-b", 8877, MICRO_B)
            service.start()

        when:
            URI uri = service.serviceResolver.fetchUri(new ServicePath('com/test/microB'))

        then:
            uri == 'http://micro-b:8877/api'.toURI()
    }

    def 'should return names of my collaborators'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "api", "micro-b", 8888, MICRO_B)
            service.start()

        when:
            Set<ServicePath> collaborators = service.serviceResolver.fetchMyDependencies()

        then:
            collaborators == [new ServicePath('com/test/microA')].toSet()
    }

    def 'fetchUri() should fail when no instance of given service is available'() {
        given:
            MicroDepsService service = new MicroDepsService(server.connectString, "api", "micro-b", 8877, MICRO_B)
            service.start()
            final String unavailableService = 'com/test/microA'

        when:
            URI uri = service.serviceResolver.fetchUri(new ServicePath(unavailableService))

        then:
            ServiceUnavailableException e = thrown(ServiceUnavailableException)
            e.message.contains(unavailableService)
    }

}
