package com.ofg.infrastructure.discovery

import com.ofg.stub.config.StubRunnerConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Import

/**
 * Configuration that under {@link com.ofg.config.BasicProfiles#TEST} and {@link com.ofg.config.BasicProfiles#DEVELOPMENT}
 * registers the {@link StubRunnerConfiguration}
 *
 * @see ZookeeperConnectorConditions
 */
@CompileStatic
@Conditional(ZookeeperConnectorConditions.InMemoryZookeeperCondition)
@Import(StubRunnerConfiguration)
class ConsumerDrivenContractConfiguration {
}
