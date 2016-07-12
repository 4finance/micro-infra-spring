package com.ofg.stub.config

import com.ofg.infrastructure.discovery.MicroserviceConfigurationNotPresentException
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.*
import com.ofg.stub.util.CollaboratorsFromZookeeper
import groovy.grape.Grape
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration that initializes a {@link BatchStubRunner} that runs {@link StubRunner} instance for each microservice's collaborator.
 *
 * Properties that can be set are
 *
 * <ul>
 *     <li>{@code stubrunner.port.range.min} default value {@code 10000} - minimal port value for the collaborator's Wiremock</li>
 *     <li>{@code stubrunner.port.range.max} default value {@code 15000} - maximal port value for the collaborator's Wiremock</li>
 *     <li>{@code stubrunner.stubs.repository.root} default value {@code 4finance Nexus Repository location} - point to where your stub mappings are uploaded as a jar</li>
 *     <li>{@code stubrunner.stubs.group} default value {@code com.ofg} - group name of your stub mappings</li>
 *     <li>{@code stubrunner.stubs.module} default value {@code stub-definitions} - artifactId of your stub mappings</li>
 *     <li>{@code stubrunner.skip-local-repo} default value {@code false} - avoids local repository in the dependency resolution & always pulls from the remote</li>
 *     <li>{@code stubrunner.use-microservice-definitions} default value {@code false} - use per microservice stub resolution rather than one jar for all microservices</li>
 *     <li>{@code stubrunner.wait-for-service} default value {@code false} - wait for connection from the service to local Zookeeper</li>
 *     <li>{@code stubrunner.wait-timeout} default value {@code 1} - sets wait-for-service timeout</li>
 * </ul>
 *
 * What happens under the hood is that
 *
 * <ul>
 *     <li>Depending on value of {@code stubrunner.use.local.repo} parameter {@link Grape} takes the latest version of stub mappings from local repository or downloads it from remote one</li>
 *     <li>The dependency is unpacked to a temporary folder</li>
 *     <li>Basing on your {@code microservice.json} setup, for each collaborator a {@link StubRunner} is set up</li>
 *     <li>Each {@link StubRunner} is initialized with a single instance of the Zookeeper {@link TestingServer}</li>
 *     <li>{@link StubRunner} takes the mappings from the unpacked JAR file</li>
 * </ul>
 *
 * @see BatchStubRunner
 * @see TestingServer
 * @see ServiceConfigurationResolver
 */
@Configuration
@Import([ZookeeperDiscoveryClientConfiguration, ServiceDiscoveryTestingServerConfiguration])
@Slf4j
@CompileStatic
class StubRunnerConfiguration {

    @Deprecated
    @Autowired(required = false)
    private ServiceConfigurationResolver serviceConfigurationResolver
    @Autowired(required = false)
    ZookeeperDependencies zookeeperDependencies
    @Autowired(required = false)
    ZookeeperDiscoveryProperties zookeeperDiscoveryProperties

    /**
     * Bean that initializes stub runners, runs them and on shutdown closes them. Upon its instantiation
     * JAR with stubs is downloaded and unpacked to a temporary folder. Next, {@link StubRunner} instance are
     * registered for each collaborator.
     *
     * @param minPortValue min port value of the Wiremock instance for the given collaborator
     * @param maxPortValue max port value of the Wiremock instance for the given collaborator
     * @param stubRepositoryRoot root URL from where the JAR with stub mappings will be downloaded
     * @param @Deprecated stubsGroup group name of the dependency containing stub mappings
     * @param @Deprecated stubsModule module name of the dependency containing stub mappings
     * @param stubsSuffix suffix for the jar containing stubs
     * @param workOffline forces offline work
     * @param @Deprecated skipLocalRepo avoids local repository in dependency resolution
     * @param useMicroserviceDefinitions use per microservice stub resolution rather than one jar for all microservices
     * @param waitForService wait for connection from service
     * @param waitTimeout wait timeout in seconds
     * @param testingServer test instance of Zookeeper
     * @param serviceConfigurationResolver object that wraps the microservice configuration
     */
    @Bean(initMethod = 'runStubs', destroyMethod = 'close')
    StubRunning batchStubRunner(@Value('${stubrunner.port.range.min:10000}') Integer minPortValue,
                                @Value('${stubrunner.port.range.max:15000}') Integer maxPortValue,
                                @Value('${stubrunner.stubs.repository.root:http://nexus.4finance.net/content/repositories/Pipeline}') String stubRepositoryRoot,
                                @Deprecated @Value('${stubrunner.stubs.group:com.ofg}') String stubsGroup,
                                @Deprecated @Value('${stubrunner.stubs.module:stub-definitions}') String stubsModule,
                                @Value('${stubrunner.stubs.suffix:stubs}') String stubsSuffx,
                                @Value('${stubrunner.stubs.classifier:}') String stubsClassifier,
                                @Value('${stubrunner.work-offline:false}') boolean workOffline,
                                @Deprecated @Value('${stubrunner.skip-local-repo:true}') boolean skipLocalRepo,
                                @Value('${stubrunner.use-microservice-definitions:false}') boolean useMicroserviceDefinitions,
                                @Value('${stubrunner.wait-for-service:false}') boolean waitForService,
                                @Value('${stubrunner.wait-timeout:1}') Integer waitTimeout,
                                TestingServer testingServer) {
        checkIfConfigurationIsPresent()
        boolean shouldWorkOnline = isPropertySetToWorkOnline(workOffline, skipLocalRepo)
        StubRunnerOptions stubRunnerOptions = new StubRunnerOptions(minPortValue, maxPortValue, stubRepositoryRoot, stubsGroup, stubsModule, shouldWorkOnline,
                useMicroserviceDefinitions, testingServer.connectString, testingServer.port, stubsSuffx, stubsClassifier, waitForService, waitTimeout)
        Collaborators dependencies = getCollaborators()
        return new BatchStubRunnerFactory(stubRunnerOptions, dependencies).buildBatchStubRunner()
    }

    private void checkIfConfigurationIsPresent() {
        if (!serviceConfigurationResolver && !zookeeperDependencies) {
            throw new MicroserviceConfigurationNotPresentException()
        }
    }

    private Collaborators getCollaborators() {
        if (zookeeperDiscoveryProperties && zookeeperDependencies) {
            return CollaboratorsFromZookeeper.fromZookeeperDependencies(zookeeperDiscoveryProperties, zookeeperDependencies)
        }
        return DescriptorToCollaborators.fromDeprecatedMicroserviceDescriptor(serviceConfigurationResolver)
    }

    private boolean isPropertySetToWorkOnline(boolean workOffline, boolean skipLocalRepo) {
        return workOffline ? false : skipLocalRepo
    }

}
