package com.ofg.stub.spring

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.Arguments
import com.ofg.stub.StubRunner
import com.ofg.stub.StubRunning
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import groovy.grape.Grape
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static groovy.grape.Grape.addResolver
import static groovy.grape.Grape.resolve
import static groovy.io.FileType.FILES
import static java.nio.file.Files.createTempDirectory

/**
 * Configuration that initializes a {@link StubRunnerConfiguration.BatchStubRunner} that loads
 * as many {@link StubRunner} instances as there are dependencies for the microservice.
 *
 * Properties that can be set are
 *
 * <ul>
 *     <li>{@code stubrunner.port.range.min} default value {@code 10000} - minimal port value for the collaborator's Wiremock</li>
 *     <li>{@code stubrunner.port.range.max} default value {@code 15000} - maximal port value for the collaborator's Wiremock</li>
 *     <li>{@code stubrunner.stubs.repository.root} default value {@code 4finance Nexus Repository location} - point to where your stub mappings are uploaded as a jar</li>
 *     <li>{@code stubrunner.stubs.group} default value {@code com.ofg} - group name of your stub mappings</li>
 *     <li>{@code stubrunner.stubs.module} default value {@code stub-definitions} - artifactId of your stub mappings</li>
 * </ul>
 *
 * What happens under the hood is that
 *
 * <ul>
 *     <li>{@link Grape} downloads the latest version of the repository containing the stub mappings</li>
 *     <li>The downloaded dependency is unpacked to a temporary folder</li>
 *     <li>Basing on your {@code microservice.json} setup, for each collaborator a {@link StubRunner} is set up</li>
 *     <li>Each {@link StubRunner} is initialized with a single instance of the Zookeeper {@link TestingServer}</li>
 *     <li>{@link StubRunner} takes the mappings from the unpacked JAR file</li>
 * </ul>
 *
 * @see StubRunnerConfiguration.BatchStubRunner
 * @see TestingServer
 * @see ServiceConfigurationResolver
 */
@Configuration
class StubRunnerConfiguration {

    private static final String LATEST_MODULE = '*'
    private static final String REPOSITORY_NAME = 'dependency-repository'
    private static final String STUB_RUNNER_TEMP_DIR_PREFIX = 'stub-runner'

    /**
     * Bean that initializes stub runners, runs them and on shutdown closes them. Upon its instantiation
     * JAR with stubs is downloaded and unpacked to a temporary folder. Next, {@link StubRunner} instance are
     * registered for each collaborator.
     *
     * @param minPortValue min port value of the Wiremock instance for the given collaborator
     * @param maxPortValue max port value of the Wiremock instance for the given collaborator
     * @param stubRepositoryRoot root URL from where the JAR with stub mappings will be downloaded
     * @param stubsGroup group name of the dependency containing stub mappings
     * @param stubsModule module name of the dependency containing stub mappings
     * @param testingServer test instance of Zookeper
     * @param serviceConfigurationResolver object that wraps the microservice configuration
     */
    @Bean(initMethod = 'runStubs', destroyMethod = 'close')
    BatchStubRunner batchStubRunner(@Value('${stubrunner.port.range.min:10000}') Integer minPortValue,
                                    @Value('${stubrunner.port.range.max:15000}') Integer maxPortValue,
                                    @Value('${stubrunner.stubs.repository.root:http://nexus.4finance.net/content/repositories/Pipeline}') String stubRepositoryRoot,
                                    @Value('${stubrunner.stubs.group:com.ofg}') String stubsGroup,
                                    @Value('${stubrunner.stubs.module:stub-definitions}') String stubsModule,
                                    TestingServer testingServer,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        URI stubJarUri = findGrabbedStubJars(stubRepositoryRoot, stubsGroup, stubsModule)
        File tmpDirWhereStubsWillBeUnzipped = unpackStubJarToATemporaryFolder(stubJarUri)
        String context = serviceConfigurationResolver.basePath
        List<StubRunner> stubRunners = serviceConfigurationResolver.dependencies.collect { String alias, String dependencyMappingsPath ->
            List<ProjectMetadata> projects = [new ProjectMetadata(alias, dependencyMappingsPath, serviceConfigurationResolver.basePath)]
            Arguments arguments = new Arguments(tmpDirWhereStubsWillBeUnzipped.path, dependencyMappingsPath, testingServer.port, minPortValue, maxPortValue, context, projects)
            return new StubRunner(arguments, new StubRegistry(testingServer))
        }
        return new BatchStubRunner(stubRunners)
    }

    private File unpackStubJarToATemporaryFolder(URI stubJarUri) {
        File tmpDirWhereStubsWillBeUnzipped = createTempDirectory(STUB_RUNNER_TEMP_DIR_PREFIX).toFile()
        tmpDirWhereStubsWillBeUnzipped.deleteOnExit()
        use(ZipCategory) {
            new File(stubJarUri).unzipTo(tmpDirWhereStubsWillBeUnzipped)
        }
        return tmpDirWhereStubsWillBeUnzipped
    }

    private URI findGrabbedStubJars(String stubRepositoryRoot, String stubsGroup, String stubsModule) {
        addResolver(name: REPOSITORY_NAME, root: stubRepositoryRoot)
        Map depToGrab = [group: stubsGroup, module: stubsModule, version: LATEST_MODULE, transitive: false]
        URI resolvedUri = resolveDependencyLocation(depToGrab)
        ensureThatLatestVersionWillBePicked(resolvedUri)
        return resolveDependencyLocation(depToGrab)
    }

    private URI resolveDependencyLocation(LinkedHashMap<String, Serializable> depToGrab) {
        return resolve([classLoader: new GroovyClassLoader()], depToGrab).first()
    }

    private void ensureThatLatestVersionWillBePicked(URI resolvedUri) {
        getStubRepositoryGrapeRoot(resolvedUri).eachFileRecurse(FILES, { if (it.name.endsWith('xml')) it.delete() })
    }

    private File getStubRepositoryGrapeRoot(URI resolvedUri) {
        return new File(resolvedUri).parentFile.parentFile
    }

    /**
     * Test instance of Zookeeper
     *
     * @param serviceResolverUrl - host with port where your application where search for Zookeeper instance
     */
    @Bean(destroyMethod = 'close')
    TestingServer testingServer(@Value('${service.resolver.url:localhost:2181}') String serviceResolverUrl) {
        return new TestingServer(new URI(prependProtocol(serviceResolverUrl)).port)
    }

    private String prependProtocol(String serviceResolverUrl) {
        switch (serviceResolverUrl) {
            case ~/^http:.*$/:
                return prependProtocol('http', serviceResolverUrl)
            case ~/^https:.*$/:
                return prependProtocol('https', serviceResolverUrl)
            default:
                return serviceResolverUrl
        }
    }

    private String prependProtocol(String protocol, String serviceResolverUrl) {
        return "$protocol://$serviceResolverUrl"
    }

    static class BatchStubRunner implements StubRunning, Closeable {

        private final List<StubRunner> stubRunners

        BatchStubRunner(List<StubRunner> stubRunners) {
            this.stubRunners = stubRunners
        }

        @Override
        void runStubs() {
            stubRunners*.runStubs()
        }

        @Override
        void close() throws IOException {
            stubRunners*.close()
        }
    }
}