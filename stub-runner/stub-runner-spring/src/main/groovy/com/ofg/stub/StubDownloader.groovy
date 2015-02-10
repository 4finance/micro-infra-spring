package com.ofg.stub

import com.ofg.stub.spring.ZipCategory
import groovy.util.logging.Slf4j

import static groovy.grape.Grape.addResolver
import static groovy.grape.Grape.resolve
import static groovy.io.FileType.FILES
import static java.nio.file.Files.createTempDirectory

@Slf4j
class StubDownloader {

    private static final String LATEST_MODULE = '*'
    private static final String REPOSITORY_NAME = 'dependency-repository'
    private static final String STUB_RUNNER_TEMP_DIR_PREFIX = 'stub-runner'

    File downloadAndUnpackStubJar(boolean skipLocalRepo, String stubRepositoryRoot, String stubsGroup, String
            stubsModule) {
        URI stubJarUri = findGrabbedStubJars(skipLocalRepo, stubRepositoryRoot, stubsGroup, stubsModule)
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
        return buildResolver(skipLocalRepo).resolveDependency(stubRepositoryRoot, depToGrab)
    }

    private DependencyResolver buildResolver(boolean skipLocalRepo) {
        skipLocalRepo ? new RemoteDependencyResolver() : new LocalFirstDependencyResolver()
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
            URI resolvedUri = resolveDependencyLocation(depToGrab)
            ensureThatLatestVersionWillBePicked(resolvedUri)
            return resolveDependencyLocation(depToGrab)
        }

        private void failureHandler(String stubRepository, String reason, Exception cause) {
            throw new DependencyResolutionException("Unable to open connection with stub repository [$stubRepository]. Reason: $reason", cause)
        }

        private void ensureThatLatestVersionWillBePicked(URI resolvedUri) {
            getStubRepositoryGrapeRoot(resolvedUri).eachFileRecurse(FILES, {
                if (it.name.endsWith('.xml')) {
                    log.info("removing ${it}"); it.delete()
                }
            })
        }

        private File getStubRepositoryGrapeRoot(URI resolvedUri) {
            return new File(resolvedUri).parentFile.parentFile
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
            log.info("Resolving dependency ${depToGrab} location in local repository...")
            URI location = resolveDependencyLocation(depToGrab)
            if (location == null) {
                log.warn("Unable to find dependency $depToGrab in local repository, trying $stubRepositoryRoot")
                location = delegate.resolveDependency(stubRepositoryRoot, depToGrab)
            }
            return location
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

    class DependencyResolutionException extends RuntimeException {

        DependencyResolutionException(String message, Throwable cause) {
            super(message, cause)
        }

    }

}
