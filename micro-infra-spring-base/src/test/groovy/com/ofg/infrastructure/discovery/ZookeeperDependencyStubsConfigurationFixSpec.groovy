package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.zookeeper.discovery.dependency.StubsConfiguration
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

import static com.ofg.config.BasicProfiles.SPRING_CLOUD

@SpringBootTest(classes = [BaseConfiguration, ZookeeperDependencyStubsConfigurationFix])
@TestPropertySource(properties = [
        'spring.application.name: io/company/department/my-service',
        'spring.cloud.zookeeper.dependencies.some-service.path: io/company/department/some-service',
        'spring.cloud.zookeeper.dependency.resttemplate.enabled: false'
])
@ActiveProfiles(SPRING_CLOUD)
class ZookeeperDependencyStubsConfigurationFixSpec extends MicroserviceMvcWiremockSpec {

    @Autowired
    ZookeeperDependencies zookeeperDependencies

    def 'should correctly define stubs configuration when stubs path is not provided'() {
        when:
            StubsConfiguration stubsConfiguration = zookeeperDependencies.getDependencyForAlias('some-service').stubsConfiguration

        then:
            stubsConfiguration.stubsGroupId == 'io.company.department'
            stubsConfiguration.stubsArtifactId == 'some-service'
            stubsConfiguration.stubsClassifier == 'stubs'
    }
}
