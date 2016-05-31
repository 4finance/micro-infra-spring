package com.ofg.stub

import com.ofg.stub.util.ZipCategory
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.repository.RepositoryPolicy
import org.eclipse.aether.resolution.*
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory

import static java.nio.file.Files.createTempDirectory

@CompileStatic
@Slf4j
class AetherStubDownloader implements StubDownloader {

    private static final String MAVEN_LOCAL_REPOSITORY_LOCATION = 'maven.repo.local'
    private static final String ACCUREST_TEMP_DIR_PREFIX = 'accurest'
    private static final String ARTIFACT_EXTENSION = 'jar'
    private static final String LATEST_ARTIFACT_VERSION = '(,]'
    private static final String LATEST_VERSION_IN_IVY = "+"

    private final List<RemoteRepository> remoteRepos
    private final RepositorySystem repositorySystem
    private final RepositorySystemSession session

    AetherStubDownloader(StubRunnerOptions stubRunnerOptions) {
        this.remoteRepos = remoteRepositories(stubRunnerOptions)
        log.info("Download using remote repositories $remoteRepos")
        this.repositorySystem = newRepositorySystem()
        this.session = newSession(this.repositorySystem, stubRunnerOptions.skipLocalRepo)
    }

    @Override
    File downloadAndUnpackStubJar(String stubsGroup, String stubsModule, String classifier) {
        String version = getVersion(stubsGroup, stubsModule, LATEST_VERSION_IN_IVY, classifier)
        return unpackedJar(version, stubsGroup, stubsModule, classifier)
    }

    private List<RemoteRepository> remoteRepositories(StubRunnerOptions stubRunnerOptions) {
        return stubRunnerOptions.stubRepositoryRoot.split(',').collect { String repo ->
            return new RemoteRepository.Builder("remote", "default", repo).build()
        }
    }

    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator()
        locator.addService(RepositoryConnectorFactory, BasicRepositoryConnectorFactory)
        locator.addService(TransporterFactory, FileTransporterFactory)
        locator.addService(TransporterFactory, HttpTransporterFactory)
        return locator.getService(RepositorySystem)
    }

    private RepositorySystemSession newSession(RepositorySystem system, boolean skipLocalRepo) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession()
        if (skipLocalRepo) {
            session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_ALWAYS)
        }
        LocalRepository localRepo = new LocalRepository(localRepositoryDirectory())
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo))
        return session
    }

    private String localRepositoryDirectory() {
        System.getProperty(MAVEN_LOCAL_REPOSITORY_LOCATION, "${System.getProperty("user.home")}/.m2/repository")
    }

    private File unpackedJar(String resolvedVersion, String stubsGroup, String stubsModule, String classifier) {
        log.info("Resolved version is [$resolvedVersion]")
        if (!resolvedVersion) {
            log.warn("Stub for group [$stubsGroup] module [$stubsModule] and classifier [$classifier] not found in $remoteRepos")
            return null
        }
        Artifact artifact = new DefaultArtifact(stubsGroup, stubsModule, classifier, ARTIFACT_EXTENSION, resolvedVersion)
        ArtifactRequest request = new ArtifactRequest(artifact: artifact, repositories: remoteRepos)
        log.info("Resolving artifact $artifact from ${remoteRepos ?: 'local maven repo'}")
        try {
            ArtifactResult result = repositorySystem.resolveArtifact(session, request)
            log.info("Resolved artifact $artifact to ${result.artifact.file} from ${result.repository}")
            return unpackStubJarToATemporaryFolder(result.artifact.file.toURI())
        } catch (Exception e) {
            log.warn("Exception occured while trying to download a stub for group [$stubsGroup] module [$stubsModule] and classifier [$classifier] in $remoteRepos", e)
            return null
        }

    }

    private String getVersion(String stubsGroup, String stubsModule, String version, String classifier) {
        if (!version || LATEST_VERSION_IN_IVY == version) {
            log.info("Desired version is [$version] - will try to resolve the latest version")
            return resolveHighestArtifactVersion(stubsGroup, stubsModule, classifier)
        }
        log.info("Will try to resolve version [$version]")
        return resolveArtifactVersion(stubsGroup, stubsModule, version, classifier)
    }

    private String resolveHighestArtifactVersion(String stubsGroup, String stubsModule, String classifier) {
        Artifact artifact = new DefaultArtifact(stubsGroup, stubsModule, classifier, ARTIFACT_EXTENSION, LATEST_ARTIFACT_VERSION)
        VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, remoteRepos, null)
        VersionRangeResult rangeResult = repositorySystem.resolveVersionRange(session, versionRangeRequest)
        if (!rangeResult.highestVersion) {
            log.error("Version was not resolved!")
        }
        return rangeResult.highestVersion ?: ''
    }

    private String resolveArtifactVersion(String stubsGroup, String stubsModule, String version, String classifier) {
        Artifact artifact = new DefaultArtifact(stubsGroup, stubsModule, classifier, ARTIFACT_EXTENSION, version)
        VersionRequest versionRequest = new VersionRequest(artifact, remoteRepos, null)
        VersionResult versionResult = repositorySystem.resolveVersion(session, versionRequest)
        return versionResult.version ?: ''
    }

    private static File unpackStubJarToATemporaryFolder(URI stubJarUri) {
        File tmpDirWhereStubsWillBeUnzipped = createTempDirectory(ACCUREST_TEMP_DIR_PREFIX).toFile()
        tmpDirWhereStubsWillBeUnzipped.deleteOnExit()
        log.info("Unpacking stub from JAR [URI: ${stubJarUri}]")
        ZipCategory.unzipTo(new File(stubJarUri), tmpDirWhereStubsWillBeUnzipped)
        return tmpDirWhereStubsWillBeUnzipped
    }

}