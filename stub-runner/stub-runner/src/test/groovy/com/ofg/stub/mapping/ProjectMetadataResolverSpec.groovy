package com.ofg.stub.mapping

import com.ofg.infrastructure.discovery.InstanceDetails
import com.ofg.stub.server.ZookeeperServer
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import spock.lang.Specification

class ProjectMetadataResolverSpec extends Specification {

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
        when:
            Collection<ProjectMetadata> metadatas = ProjectMetadataResolver.resolveFromZookeeper('testService', 'pl', zookeeperServer)
        then:
            metadatas.every { it.context == 'pl' && it.projectName == 'testService' }
            metadatas.size() == 2
            metadatas.contains(new ProjectMetadata('testService', 'com/ofg/ping', 'pl'))
            metadatas.contains(new ProjectMetadata('testService', 'com/ofg/pong', 'pl'))
        cleanup:
            serviceDiscovery?.close()
            zookeeperServer?.shutdown()
            server?.close()
    }

    private ServiceDiscovery buildServiceDiscovery(CuratorFramework curatorFramework) {
        ServiceInstance serviceInstance = ServiceInstance.builder()
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .address('localhost')
                    .port(21820)
                    .name('testService')
                    .payload(instanceDetails())
                    .build()
        return ServiceDiscoveryBuilder.builder(InstanceDetails)
                    .basePath('pl')
                    .client(curatorFramework)
                    .thisInstance(serviceInstance)
                    .build()
    }

    InstanceDetails instanceDetails() {
        return new InstanceDetails(['com/ofg/ping', 'com/ofg/pong'])
    }

}
