package com.ofg.stub
import com.github.tomakehurst.wiremock.WireMockServer
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.server.ZookeeperServer
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class ProjectMetadataResolverSpec extends Specification {

    private static final Integer COLLABORATOR_URL = 21820
    private static final String TEST_SERVICE_PATH = 'com/ofg/testService'

    def 'should resolve project metadata from Zookeeper server'() {
        given:
            TestingServer server = new TestingServer(21819)
            server.start()
        and:
            ZookeeperServer zookeeperServer = new ZookeeperServer(server.connectString)
            zookeeperServer.start()
        and:
            ServiceDiscovery serviceDiscovery = buildServiceDiscovery(zookeeperServer.curatorFramework)
            serviceDiscovery.start()
        and:
            WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(COLLABORATOR_URL))
            wireMockServer.start()
        and:
            wireMockServer.givenThat(get(urlEqualTo('/microservice.json')).willReturn(aResponse().withBody(ProjectMetadataResolverSpec.getResource('/microservice_example.json').text)))
        when:
            ServiceConfigurationResolver serviceConfigurationResolver = CollaboratorsPathResolver.resolveFromZookeeper(TEST_SERVICE_PATH, 'pl', zookeeperServer, new StubRunnerOptions())
        then:
            serviceConfigurationResolver.basePath == 'pl'
        and:
            List<String> collaboratorsPaths = serviceConfigurationResolver.dependencies.collect { it.servicePath.path }
            collaboratorsPaths.size() == 2
            collaboratorsPaths.containsAll(['com/ofg/ping', 'com/ofg/pong'])
        cleanup:
            serviceDiscovery?.close()
            zookeeperServer?.shutdown()
            server?.close()
            wireMockServer?.shutdown()
    }

    private ServiceDiscovery buildServiceDiscovery(CuratorFramework curatorFramework) {
        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address('localhost')
                .port(COLLABORATOR_URL)
                .name(TEST_SERVICE_PATH)
                .build()
        return ServiceDiscoveryBuilder.builder(Map)
                .basePath('pl')
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
    }


}
