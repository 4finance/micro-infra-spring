package com.ofg.stub.config

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.Arguments
import com.ofg.stub.BatchStubRunner
import com.ofg.stub.StubRunner
import com.ofg.stub.StubRunning
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import com.ofg.stub.spring.ZipCategory
import groovy.grape.Grape
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

import static groovy.grape.Grape.addResolver
import static groovy.grape.Grape.resolve
import static groovy.io.FileType.FILES
import static groovyx.net.http.Method.HEAD
import static java.nio.file.Files.createTempDirectory
import static com.ofg.infrastructure.discovery.ServiceConfigurationProperties.PATH

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
 *     <li>{@code stubrunner.use.local.repo} default value {@code false} - points whether dependencies should be resolved against local or remote repository</li>
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
@Import(ServiceDiscoveryTestingServerConfiguration)
@Slf4j
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
     * @param useLocalRepo points whether dependencies should be resolved against local or remote repository
     * @param testingServer test instance of Zookeeper
     * @param serviceConfigurationResolver object that wraps the microservice configuration
     */
    @Bean(initMethod = 'runStubs', destroyMethod = 'close')
    StubRunning batchStubRunner(@Value('${stubrunner.port.range.min:10000}') Integer minPortValue,
                                    @Value('${stubrunner.port.range.max:15000}') Integer maxPortValue,
                                    @Value('${stubrunner.stubs.repository.root:http://nexus.4finance.net/content/repositories/Pipeline}') String stubRepositoryRoot,
                                    @Value('${stubrunner.stubs.group:com.ofg}') String stubsGroup,
                                    @Value('${stubrunner.stubs.module:stub-definitions}') String stubsModule,
                                    @Value('${stubrunner.use.local.repo:false}') boolean useLocalRepo,
                                    TestingServer testingServer,
                                    ServiceConfigurationResolver serviceConfigurationResolver) {
        URI stubJarUri = findGrabbedStubJars(useLocalRepo, stubRepositoryRoot, stubsGroup, stubsModule)
        File unzippedStubsDir = unpackStubJarToATemporaryFolder(stubJarUri)
        String context = serviceConfigurationResolver.basePath
        List<StubRunner> stubRunners = serviceConfigurationResolver.dependencies.collect { String alias, Map dependencyConfig ->
            String dependencyMappingsPath = dependencyConfig[PATH]
            List<ProjectMetadata> projects = [new ProjectMetadata(alias, dependencyMappingsPath, serviceConfigurationResolver.basePath)]
            Arguments arguments = new Arguments(unzippedStubsDir.path, dependencyMappingsPath, testingServer.port, minPortValue, maxPortValue, context, projects)
            return new StubRunner(arguments, new StubRegistry(testingServer))
        }
        return new BatchStubRunner(stubRunners)
    }

    private File unpackStubJarToATemporaryFolder(URI stubJarUri) {
        File tmpDirWhereStubsWillBeUnzipped = createTempDirectory(STUB_RUNNER_TEMP_DIR_PREFIX).toFile()
        tmpDirWhereStubsWillBeUnzipped.deleteOnExit()
        log.debug("Unpacking stub from JAR [URI: ${stubJarUri}]")
        use(ZipCategory) {
            new File(stubJarUri).unzipTo(tmpDirWhereStubsWillBeUnzipped)
        }
        return tmpDirWhereStubsWillBeUnzipped
    }

    private URI findGrabbedStubJars(boolean useLocalRepo, String stubRepositoryRoot, String stubsGroup, String stubsModule) {
        Map depToGrab = [group: stubsGroup, module: stubsModule, version: LATEST_MODULE, transitive: false]
        return buildResolver(useLocalRepo).resolveDependency(stubRepositoryRoot, depToGrab)
    }

    /**
     * Provides {@link DependencyResolver} implementation.
     *
     * @param useLocalRepo points whether a {@link DependencyResolver} should be provided that works on local Grape repository (when set to {@code true) or remote one (if set to {@code false})
     *
     * @return instance of {@link DependencyResolver}
     */
    DependencyResolver buildResolver(boolean useLocalRepo) {
        if (useLocalRepo) {
            return new LocalDependencyResolver()
        } else {
            return new RemoteDependencyResolver()
        }
    }

    /**
     * Dependency resolver providing {@link URI} to remote dependencies.
     */
    private class RemoteDependencyResolver extends DependencyResolver {

        URI resolveDependency(String stubRepositoryRoot, Map depToGrab) {
            try {
                checkConnectivityWithRemoteRepository(stubRepositoryRoot, depToGrab)
            }
            catch (UnknownHostException e) {
                failureHandler(stubRepositoryRoot, "unknown host error -> ${e.message}", e)
            }
            catch (Exception e) {
                failureHandler(stubRepositoryRoot, "connection error -> ${e.message}", e)
            }
        }

        private void checkConnectivityWithRemoteRepository(String stubRepositoryRoot, Map depToGrab) {
            def http = new HTTPBuilder(stubRepositoryRoot)
            http.request(HEAD) { req ->
                response.success = { resp ->
                    log.info("Connection with [$stubRepositoryRoot] succeeded")
                    return doResolveRemoteDependency(stubRepositoryRoot, depToGrab)
                }
                response.failure = { resp ->
                    failureHandler(stubRepositoryRoot, "Status code [${resp.status}]")
                }
            }
        }

        private URI doResolveRemoteDependency(String stubRepositoryRoot, Map depToGrab) {
            addResolver(name: REPOSITORY_NAME, root: stubRepositoryRoot)
            log.info("Resolving dependency ${depToGrab} location in remote repository...")
            URI resolvedUri = resolveDependencyLocation(depToGrab)
            ensureThatLatestVersionWillBePicked(resolvedUri)
            return resolveDependencyLocation(depToGrab)
        }

        private void failureHandler(String stubRepository, String reason, Exception cause) {
            throw new RuntimeException("Unable to open connection with stub repository [$stubRepository]. Reason: $reason", cause)
        }

        private void ensureThatLatestVersionWillBePicked(URI resolvedUri) {
            getStubRepositoryGrapeRoot(resolvedUri).eachFileRecurse(FILES, { if (it.name.endsWith('xml')) {log.info("removing ${it}");it.delete()} })
        }

        private File getStubRepositoryGrapeRoot(URI resolvedUri) {
            return new File(resolvedUri).parentFile.parentFile
        }

    }

    /**
     * Dependency resolver providing {@link URI} to dependency stored locally.
     */
    private class LocalDependencyResolver extends DependencyResolver {

        URI resolveDependency(String stubRepositoryRoot, Map depToGrab) {
            log.warn("Resolving dependency ${depToGrab} location in local repository...")
            return resolveDependencyLocation(depToGrab)
        }

    }

    /**
     * Base class of dependency resolvers providing {@link URI} to required dependency.
     */
    abstract class DependencyResolver {

        /**
         * Returns {@link URI} to a dependency.
         *
         * @param stubRepositoryRoot root of the repository where the dependency should be found
         * @param depToGrab parameters describing dependency to search for
         *
         * @return {@link URI} to dependency
         */
        abstract URI resolveDependency(String stubRepositoryRoot, Map depToGrab)

        URI resolveDependencyLocation(Map depToGrab) {
            return resolve([classLoader: new GroovyClassLoader()], depToGrab).first()
        }

    }
}
