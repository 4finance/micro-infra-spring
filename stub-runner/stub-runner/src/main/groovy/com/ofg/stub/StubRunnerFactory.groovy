package com.ofg.stub

import com.google.common.base.Function
import com.google.common.base.Optional
import com.ofg.stub.mapping.ProjectMetadata
import com.ofg.stub.registry.StubRegistry
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry

import static com.google.common.base.Preconditions.checkArgument
import static com.ofg.infrastructure.discovery.MicroserviceConfiguration.Dependency.StubsConfiguration
import static org.apache.commons.lang.StringUtils.*

@Slf4j
@CompileStatic
@PackageScope
class StubRunnerFactory {

    private static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500)

    private final StubRunnerOptions stubRunnerOptions
    private final Collaborators collaborators
    private final StubDownloader stubDownloader
    private final CuratorFramework client

    StubRunnerFactory(StubRunnerOptions stubRunnerOptions, Collaborators collaborators) {
        this(stubRunnerOptions, collaborators, CuratorFrameworkFactory.newClient(stubRunnerOptions.zookeeperConnectString, RETRY_POLICY),
                new AetherStubDownloader(stubRunnerOptions))
    }

    @PackageScope
    StubRunnerFactory(StubRunnerOptions stubRunnerOptions, Collaborators collaborators,
                      CuratorFramework client, StubDownloader stubDownloader) {
        this.stubRunnerOptions = stubRunnerOptions
        this.client = client
        this.collaborators = collaborators
        this.stubDownloader = stubDownloader
    }

    List<Optional<StubRunner>> createStubsFromServiceConfiguration() {
        client.start()
        return collaborators.collaboratorsPath.collect { String dependencyMappingsPath ->
            StubDependency module = getModuleForDependency(dependencyMappingsPath)
            final File unzippedStubDir = getUnzippedStubDir(module)
            final String context = collaborators.basePath
            return createStubRunner(unzippedStubDir, extractServiceNameFromPath(dependencyMappingsPath), context, dependencyMappingsPath)
        }
    }

    private File getUnzippedStubDir(StubDependency module) {
        return stubDownloader.downloadAndUnpackStubJar(module.groupId, module.artifactId, module.classifier) ?:
                stubDownloader.downloadAndUnpackStubJar(module.groupId, module.artifactId, getClassifierIfMissing())
    }

    private String getClassifierIfMissing() {
        return stubRunnerOptions.stubClassifier != null ? stubRunnerOptions.stubClassifier : 'stubs'
    }

    private StubDependency getModuleForDependency(String dependencyMappingsPath) {
        return Optional.fromNullable(collaborators.stubsPaths.get(dependencyMappingsPath))
                .transform(STUBS_TO_DEPENDENCY)
                .or(createModuleFromDependencyMappingsPath(dependencyMappingsPath))
    }

    private StubDependency createModuleFromDependencyMappingsPath(String dependencyMappingsPath) {
        return new StubDependency(dependencyMappingsPath + getStubDefinitionSuffix(), stubRunnerOptions.stubClassifier)
    }

    private String getStubDefinitionSuffix() {
        return stubRunnerOptions.stubDefinitionSuffix ? "-${stubRunnerOptions.stubDefinitionSuffix}" : ""
    }

    List<Optional<StubRunner>> createStubsFromStubsModule() {
        checkArgument(isNotEmpty(stubRunnerOptions.stubsGroup))
        checkArgument(isNotEmpty(stubRunnerOptions.stubsModule))
        client.start()
        final File unzippedStubsDir = stubDownloader.downloadAndUnpackStubJar(
                stubRunnerOptions.stubsGroup, stubRunnerOptions.stubsModule, stubRunnerOptions.stubClassifier)
        final String context = collaborators.basePath
        return collaborators.collaboratorsPath.collect { String dependencyMappingsPath ->
            return createStubRunner(unzippedStubsDir, extractServiceNameFromPath(dependencyMappingsPath), context, dependencyMappingsPath)
        }
    }

    private Optional createStubRunner(File unzippedStubDir, String dependencyName, String context, String dependencyMappingsPath) {
        if (!unzippedStubDir) {
            return Optional.absent()
        }
        return Optional.of(createStubRunner(dependencyName, unzippedStubDir, context, dependencyMappingsPath, stubRunnerOptions, client))
    }

    private static
    final Function<StubsConfiguration, StubDependency> STUBS_TO_DEPENDENCY = new Function<StubsConfiguration, StubDependency>() {
        @Override
        StubDependency apply(StubsConfiguration configuration) {
            return new StubDependency(configuration.stubsGroupId, configuration.stubsArtifactId, configuration.stubsClassifier)
        }
    }

    private StubRunner createStubRunner(String dependencyName, File unzippedStubsDir, String context, String dependencyMappingsPath, StubRunnerOptions stubRunnerOptions, CuratorFramework client) {
        List<ProjectMetadata> projects = [new ProjectMetadata(dependencyName, dependencyMappingsPath, context)]
        Arguments arguments = new Arguments(stubRunnerOptions, context, unzippedStubsDir.path, dependencyName, projects)
        return new StubRunner(arguments, new StubRegistry(stubRunnerOptions.zookeeperConnectString, client))
    }

    private static String extractServiceNameFromPath(String dependencyPath) {
        return substringAfterLast(dependencyPath, "/")
    }

    private static class StubDependency {
        final String groupId
        final String artifactId
        final String classifier

        StubDependency(String dependencyMappingsPath, String classifier) {
            String dependencyInPackageNotation = dependencyMappingsPath.replaceAll("/", ".")
            groupId = substringBeforeLast(dependencyInPackageNotation, ".")
            artifactId = substringAfterLast(dependencyInPackageNotation, ".")
            this.classifier = classifier
        }

        StubDependency(String groupId, String artifactId, String classifier) {
            this.groupId = groupId
            this.artifactId = artifactId
            this.classifier = classifier
        }
    }

}
