package com.ofg.infrastructure.discovery

import com.ofg.config.BasicProfiles
import groovy.transform.CompileStatic
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH

/**
 * Class holding configuration to Zookeeper server, Zookeeper service instance and to Curator framework.
 *
 * All the beans are available only in the {@link BasicProfiles#PRODUCTION} profile.
 * 
 * @see CuratorFramework
 * @see ServiceInstance
 * @see ServiceDiscovery
 */
@CompileStatic
@Import(ConsumerDrivenContractConfiguration)
@Configuration
class ServiceDiscoveryInfrastructureConfiguration {

    @Bean(initMethod = 'start', destroyMethod = 'close')
    CuratorFramework curatorFramework(ZookeeperConnector zookeeperConnector,
                                      @Value('${service.resolver.connection.retry.times:5}') int numberOfRetries,
                                      @Value('${service.resolver.connection.retry.wait:1000}') int sleepMsBetweenRetries) {
        return CuratorFrameworkFactory.newClient(zookeeperConnector.serviceResolverUrl, new RetryNTimes(numberOfRetries, sleepMsBetweenRetries))
    }

    @Bean
    @Conditional(ZookeeperConnectorConditions.StandaloneZookeeperCondition)
    ZookeeperConnector standaloneZookeeperConnector(@Value('${service.resolver.url:localhost:2181}') String serviceResolverUrl) {
        return new ZookeeperConnector() {
            @Override
            String getServiceResolverUrl() {
                return serviceResolverUrl
            }
        }
    }

    @Bean
    @Conditional(ZookeeperConnectorConditions.InMemoryZookeeperCondition)
    ZookeeperConnector inMemoryZookeeperConnector(TestingServer testingServer) {
        return new ZookeeperConnector() {
            @Override
            String getServiceResolverUrl() {
                return testingServer.connectString
            }
        }
    }

    @Bean
    ServiceInstance serviceInstance(MicroserviceAddressProvider addressProvider,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        return ServiceInstance.builder().uriSpec(new UriSpec('{scheme}://{address}:{port}'))
                                        .address(addressProvider.host)
                                        .port(addressProvider.port)
                                        .name(serviceConfigurationResolver.microserviceName)
                                        .payload(instanceDetails(serviceConfigurationResolver))
                                        .build()
    }

    @Bean(initMethod = 'start', destroyMethod = 'close')
    ServiceDiscovery serviceDiscovery(CuratorFramework curatorFramework, 
                                      ServiceInstance serviceInstance,
                                      ServiceConfigurationResolver serviceConfigurationResolver) {
        return ServiceDiscoveryBuilder
                .builder(InstanceDetails)
                .basePath('/' + serviceConfigurationResolver.basePath)
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
    }

    private InstanceDetails instanceDetails(ServiceConfigurationResolver configurationResolver) {
        List<String> dependenciesList = configurationResolver.dependencies.collect { entry ->
            entry.value[PATH] as String
        }
        return new InstanceDetails(dependenciesList)
    }
}
