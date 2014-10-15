package com.ofg.infrastructure.discovery

import com.ofg.stub.spring.StubRunnerConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

import static com.ofg.config.BasicProfiles.DEVELOPMENT
import static com.ofg.config.BasicProfiles.TEST

/**
 * Configuration that under {@link com.ofg.config.BasicProfiles#TEST and com.ofg.config.BasicProfiles#DEVELOPMENT}
 * registers the {@link StubRunnerConfiguration}
 */
@CompileStatic
@Profile([TEST, DEVELOPMENT])
@Import(StubRunnerConfiguration)
class ConsumerDrivenContractConfiguration {
}
