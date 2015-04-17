package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import com.ofg.infrastructure.discovery.ZookeeperConnectorConditions.InMemoryZookeeperCondition;
import com.ofg.stub.config.StubRunnerConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * Configuration that under {@link BasicProfiles#TEST} and {@link BasicProfiles#DEVELOPMENT}
 * registers the {@link StubRunnerConfiguration}
 *
 * @see ZookeeperConnectorConditions
 */
@Conditional(InMemoryZookeeperCondition.class)
@Import(StubRunnerConfiguration.class)
public class ConsumerDrivenContractConfiguration {
}
