package com.ofg.infrastructure.stub

import com.netflix.loadbalancer.Server
import com.netflix.loadbalancer.ServerList
import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.discovery.ServiceAlias
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile([BasicProfiles.DEVELOPMENT, BasicProfiles.TEST])
class StubConfiguration {

    @Autowired(required = false) ZookeeperDependencies zookeeperDependencies

    @Bean @Primary @Profile(BasicProfiles.SPRING_CLOUD)
    ServerList<Server> localhostServer(Stubs stubs) {
        if (zookeeperDependencies) {
            final List<Server> listOfServers = zookeeperDependencies.dependencies.keySet().collect { String alias ->
                Stub stub = stubs.of(new ServiceAlias(alias))
                return new Server(stub.host, stub.port)
            }
            return staticServer(listOfServers)
        }
        return staticServer(Arrays.asList(new Server("localhost")))
    }

    private ServerList<Server> staticServer(List<Server> listOfServers) {
        return new ServerList<Server>() {
            @Override
            List<Server> getInitialListOfServers() {
                return listOfServers
            }

            @Override
            List<Server> getUpdatedListOfServers() {
                return listOfServers
            }
        }
    }
}
