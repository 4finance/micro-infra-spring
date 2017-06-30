package com.ofg.infrastructure.discovery

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependency
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import static com.ofg.config.BasicProfiles.SPRING_CLOUD

/**
 * Workaround for bug in spring-cloud-zookeeper
 * https://github.com/spring-cloud/spring-cloud-zookeeper/issues/138
 */
@Configuration
@Profile(SPRING_CLOUD)
class ZookeeperDependencyStubsConfigurationFix implements BeanPostProcessor {

    @Override
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ZookeeperDependencies) {
            ZookeeperDependencies zookeeperDependencies = (ZookeeperDependencies) bean
            zookeeperDependencies.getDependencyConfigurations().each { ZookeeperDependency dependency ->
                if (!dependency.stubs) {
                    ServicePath servicePath = new ServicePath(dependency.path)
                    MicroserviceConfiguration.Dependency.StubsConfiguration stubConfiguration =
                            new MicroserviceConfiguration.Dependency.StubsConfiguration(servicePath)
                    dependency.stubs = stubConfiguration.toColonSeparatedDependencyNotation()
                }
            }
        }
        return bean
    }

    @Override
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean
    }

}
