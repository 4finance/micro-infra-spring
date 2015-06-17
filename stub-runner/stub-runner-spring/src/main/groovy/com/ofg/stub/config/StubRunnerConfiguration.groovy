package com.ofg.stub.config
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.*
import groovy.grape.Grape
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
/**
 * Configuration that initializes a {@link BatchStubRunner} that runs {@link StubRunner} instance for each microservice's collaborator.
 *
 * What happens under the hood is that
 *
 * <ul>
 *     <li>Depending on value of offline work parameter {@link Grape} takes the latest version of stub mappings from local repository or downloads it from remote one</li>
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
@Import(ServiceDiscoveryTestingServerConfiguration)
@Slf4j
@CompileStatic
class StubRunnerConfiguration {

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
                                @Value('${stubrunner.work-offline:false}') boolean workOffline,
                                @Value('${stubrunner.use-microservice-definitions:false}') boolean useMicroserviceDefinitions,
                                @Value('${stubrunner.wait-for-service:false}') boolean waitForService,
                                @Value('${stubrunner.wait-timeout:1}') Integer waitTimeout,
                                TestingServer testingServer,
                                ServiceConfigurationResolver serviceConfigurationResolver) {
        StubRunnerOptions stubRunnerOptions = new StubRunnerOptions(minPortValue, maxPortValue, stubRepositoryRoot, stubsGroup, stubsModule, workOffline,
                useMicroserviceDefinitions, testingServer.connectString, testingServer.port, stubsSuffx, waitForService, waitTimeout)
        List<String> dependenciesPath = serviceConfigurationResolver.dependencies.collect { it.servicePath.path }
        Collaborators dependencies = new Collaborators(serviceConfigurationResolver.basePath, dependenciesPath)
        return new BatchStubRunnerFactory(stubRunnerOptions, dependencies).buildBatchStubRunner()
    }

}
