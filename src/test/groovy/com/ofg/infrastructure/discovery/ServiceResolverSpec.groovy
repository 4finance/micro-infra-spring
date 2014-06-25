package com.ofg.infrastructure.discovery
import com.ofg.infrastructure.base.Samples
import com.ofg.infrastructure.base.ZookeeperSpec
import com.ofg.infrastructure.discovery.watcher.DefaultDependencyPresenceOnStartupChecker
import com.ofg.infrastructure.discovery.watcher.DependencyWatcher
import com.ofg.infrastructure.discovery.watcher.NoInstancesRunningException
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

import static org.codehaus.groovy.runtime.StackTraceUtils.extractRootCause

class ServiceResolverSpec extends ZookeeperSpec {
    
    ServiceConfigurationResolver serviceConfigurationResolver = new ServiceConfigurationResolver(Samples.MICROSERVICE_CONFIG)
    
    def 'should throw exception if obligatory dependencies are missing'() {
        given:
            ServiceInstance<Void> serviceInstance = ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/"))
                    .address('anyUrl')
                    .port(10)
                    .name(serviceConfigurationResolver.microserviceName)
                    .build()
            CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(server.connectString, new RetryNTimes(5, 500))
            curatorFramework.start()
            ServiceDiscovery serviceDiscovery = ServiceDiscoveryBuilder.builder(Void).basePath(serviceConfigurationResolver.basePath).client(curatorFramework).thisInstance(serviceInstance).build()
            serviceDiscovery.start()
            DependencyWatcher dependencyWatcher = new DependencyWatcher(serviceConfigurationResolver.dependencies, serviceDiscovery, [:].withDefault { new DefaultDependencyPresenceOnStartupChecker() } )

        when:
            dependencyWatcher.registerDependencies()    
        then:
            Throwable thrown = thrown(Throwable)
            extractRootCause(thrown).class == NoInstancesRunningException
        cleanup:
            curatorFramework.close()
            serviceDiscovery.close()
    }
    
}
