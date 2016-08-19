package com.ofg.infrastructure.discovery;

import com.google.common.collect.Iterables;
import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependency;

import java.util.*;

public class SpringCloudToMicroserviceJsonConverter {

    private final String servicePath;
    private final ZookeeperDependencies zookeeperDependencies;
    private final ZookeeperDiscoveryProperties zookeeperDiscoveryProperties;

    public SpringCloudToMicroserviceJsonConverter(String servicePath, ZookeeperDependencies zookeeperDependencies, ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        this.servicePath = servicePath;
        this.zookeeperDependencies = zookeeperDependencies;
        this.zookeeperDiscoveryProperties = zookeeperDiscoveryProperties;
    }

    public String toMicroserviceJsonNotation() {
        String basePath = zookeeperDiscoveryProperties.getRoot().replaceAll("/", "");
        List<MicroserviceConfiguration.Dependency> dependencies = new ArrayList<>();
        for (Map.Entry<String, ZookeeperDependency> zookeeperDependencyEntry : zookeeperDependencies.getDependencies().entrySet()) {
            ZookeeperDependency zookeeperDependency = zookeeperDependencyEntry.getValue();
            MicroserviceConfiguration.Dependency dependency = new MicroserviceConfiguration.Dependency(
                    new ServiceAlias(zookeeperDependencyEntry.getKey()),
                    new ServicePath(zookeeperDependency.getPath()),
                    zookeeperDependency.isRequired(),
                    LoadBalancerType.fromName(zookeeperDependency.getLoadBalancerType().name()),
                    zookeeperDependency.getContentTypeTemplate(),
                    zookeeperDependency.getVersion(),
                    convertFromMapOfCollection(zookeeperDependency.getHeaders()),
                    zookeeperDependency.getStubsConfiguration() == null
                            ? null
                            : new MicroserviceConfiguration.Dependency.StubsConfiguration(
                            zookeeperDependency.getStubsConfiguration().getStubsGroupId(),
                            zookeeperDependency.getStubsConfiguration().getStubsArtifactId(),
                            zookeeperDependency.getStubsConfiguration().getStubsClassifier())
            );
            dependencies.add(dependency);
        }
        MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(new ServicePath(servicePath), dependencies);
        return MicroserviceConfigurationToJsonConverter.fromConfiguration(basePath, microserviceConfiguration);
    }

    /**
     * In micro-infra we don't support collection in headers
     * @param map
     * @return
     */
    private Map<String, String> convertFromMapOfCollection(Map<String, Collection<String>> map) {
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, Collection<String>> entry : map.entrySet()) {
            newMap.put(entry.getKey(), Iterables.getFirst(entry.getValue(), StringUtils.EMPTY));
        }
        return newMap;
    }
}
