package com.ofg.stub
import com.ofg.stub.util.ZipCategory
import groovy.util.logging.Slf4j

import static groovy.grape.Grape.addResolver
import static groovy.grape.Grape.resolve
import static java.nio.file.Files.createTempDirectory
/**
 * Downloads stubs from an external repository and unpacks them locally
 */
@Slf4j
class StubDownloader {

    private static final String LATEST_MODULE = '*'
    private static final String REPOSITORY_NAME = 'dependency-repository'
    private static final String STUB_RUNNER_TEMP_DIR_PREFIX = 'stub-runner'
    private static final String GRAPE_CONFIG = 'grape.config'
    private static final String MICRO_INFRA_GRAPE_CONFIG = "micro.infra.grape.config"

    /**
     * Downloads stubs from an external repository and unpacks them locally.
     * Depending on the switch either uses only local repository to check for
     * stub presence.
     *
     * @param skipLocalRepo -flag that defines whether only local cache should be used
     * @param stubRepositoryRoot - address of the repo from which deps should be grabbed
     * @param stubsGroup - group name of the jar containing stubs
     * @param stubsModule - module name of the jar containing stubs
     * @return file where the stubs where unpacked
     */
    File downloadAndUnpackStubJar(boolean skipLocalRepo, String stubRepositoryRoot, String stubsGroup, String
            stubsModule) {
        log.warn("Downloading stubs for group [$stubsGroup] and module [$stubsModule] from repository [$stubRepositoryRoot]")
        URI stubJarUri = findGrabbedStubJars(skipLocalRepo, stubRepositoryRoot, stubsGroup, stubsModule)
        if (!stubJarUri) {
            log.warn("Failed to download stubs for group [$stubsGroup] and module [$stubsModule] from repository [$stubRepositoryRoot]")
            return null
        }
        File unzippedStubsDir = unpackStubJarToATemporaryFolder(stubJarUri)
        return unzippedStubsDir
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

    private URI findGrabbedStubJars(boolean skipLocalRepo, String stubRepositoryRoot, String stubsGroup, String stubsModule) {
        Map depToGrab = [group: stubsGroup, module: stubsModule, version: LATEST_MODULE, transitive: false]
        String microInfraGrapePath = System.getProperty(MICRO_INFRA_GRAPE_CONFIG, getDefaultMicroInfraGrapeConfigPath())
        initializeMicroInfraGrapeIfAbsent(microInfraGrapePath)
        String oldGrapeConfig = System.getProperty(GRAPE_CONFIG)
        try {
            System.setProperty(GRAPE_CONFIG, microInfraGrapePath)
            log.info("Setting default grapes path to [$microInfraGrapePath]")
            return buildResolver(skipLocalRepo).resolveDependency(stubRepositoryRoot, depToGrab)
        } finally {
            restoreOldGrapeConfigIfApplicable(oldGrapeConfig)
        }
    }

    private DependencyResolver buildResolver(boolean skipLocalRepo) {
        return skipLocalRepo ? new RemoteDependencyResolver() : new LocalFirstDependencyResolver()
    }

    private void initializeMicroInfraGrapeIfAbsent(String microInfraGrapePath) {
        File microInfraGrape = new File(microInfraGrapePath)
        if (!microInfraGrape.exists()) {
            microInfraGrape.parentFile.mkdirs()
            microInfraGrape.createNewFile()
            microInfraGrape.text = StubDownloader.class.getResource('/microInfraGrapeConfig.xml').text
        }
    }

    private void restoreOldGrapeConfigIfApplicable(String oldGrapeConfig) {
        if (oldGrapeConfig) {
            System.setProperty(GRAPE_CONFIG, oldGrapeConfig)
        }
    }

    private String getDefaultMicroInfraGrapeConfigPath() {
        return "${System.getProperty('user.home')}/.micro-infra-spring/microInfraGrapeConfig.xml"
    }

    /**
     * Dependency resolver providing {@link URI} to remote dependencies.
     */
    @Slf4j
    private class RemoteDependencyResolver extends DependencyResolver {

        URI resolveDependency(String stubRepositoryRoot, Map depToGrab) {
            try {
                return doResolveRemoteDependency(stubRepositoryRoot, depToGrab)
            } catch (UnknownHostException e) {
                failureHandler(stubRepositoryRoot, "unknown host error -> ${e.message}", e)
            } catch (Exception e) {
                failureHandler(stubRepositoryRoot, "connection error -> ${e.message}", e)
            }
        }

        private URI doResolveRemoteDependency(String stubRepositoryRoot, Map depToGrab) {
            addResolver(name: REPOSITORY_NAME, root: stubRepositoryRoot)
            log.info("Resolving dependency ${depToGrab} location in remote repository...")
            return resolveDependencyLocation(depToGrab)
        }

        private void failureHandler(String stubRepository, String reason, Exception cause) {
            log.error("Unable to resolve dependency in stub repository [$stubRepository]. Reason: [$reason]", cause)
        }

    }

    /**
     * Dependency resolver that first checks if a dependency is available in the local repository.
     * If not, it will try to provide {@link URI} from the remote repository.
     *
     * @see RemoteDependencyResolver
     */
    @Slf4j
    private class LocalFirstDependencyResolver extends DependencyResolver {

        private DependencyResolver delegate = new RemoteDependencyResolver()

        URI resolveDependency(String stubRepositoryRoot, Map depToGrab) {
            try {
                log.info("Resolving dependency ${depToGrab} location in local repository...")
                return resolveDependencyLocation(depToGrab)
            } catch (Exception e) { //Grape throws ordinary RuntimeException
                log.warn("Unable to find dependency $depToGrab in local repository, trying $stubRepositoryRoot")
                log.debug("Unable to find dependency $depToGrab in local repository: ${e.getClass()}: ${e.message}")
                return delegate.resolveDependency(stubRepositoryRoot, depToGrab)
            }
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
