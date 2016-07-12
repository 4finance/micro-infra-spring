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
            Module module = getModuleForDependency(dependencyMappingsPath)
            final File unzippedStubDir = getUnzippedStubDir(module)
            final String context = collaborators.basePath
            return createStubRunner(unzippedStubDir, module, context, dependencyMappingsPath)
        }
    }

    private File getUnzippedStubDir(Module module) {
        return stubDownloader.downloadAndUnpackStubJar(module.groupId, module.artifactId + getStubDefinitionSuffix(), module.classifier) ?:
                stubDownloader.downloadAndUnpackStubJar(module.groupId, module.artifactId + getStubDefinitionSuffix(), getClassifierIfMissing())
    }

    private String getClassifierIfMissing() {
        return stubRunnerOptions.stubClassifier != null ? stubRunnerOptions.stubClassifier : 'stubs'
    }

    private Module getModuleForDependency(String dependencyMappingsPath) {
        return Optional.fromNullable(collaborators.stubsPaths.get(dependencyMappingsPath))
                .transform(STUBS_TO_MODULE)
                .or(createModuleFromDependencyMappingsPath(dependencyMappingsPath))
    }

    private Module createModuleFromDependencyMappingsPath(String dependencyMappingsPath) {
        return new Module(dependencyMappingsPath, stubRunnerOptions.stubClassifier)
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
            Module module = getModuleForDependency(dependencyMappingsPath)
            return createStubRunner(unzippedStubsDir, module, context, dependencyMappingsPath)
        }
    }

    private Optional createStubRunner(File unzippedStubDir, Module module, String context, String dependencyMappingsPath) {
        if (!unzippedStubDir) {
            return Optional.absent()
        }
        return Optional.of(createStubRunner(module.artifactId, unzippedStubDir, context, dependencyMappingsPath, stubRunnerOptions, client))
    }

    private static final Function<StubsConfiguration, Module> STUBS_TO_MODULE = new Function<StubsConfiguration, Module>() {
        @Override
        Module apply(StubsConfiguration configuration) {
            return new Module(configuration.stubsGroupId, configuration.stubsArtifactId, configuration.stubsClassifier)
        }
    }

    private StubRunner createStubRunner(String alias, File unzippedStubsDir, String context, String dependencyMappingsPath, StubRunnerOptions stubRunnerOptions, CuratorFramework client) {
        List<ProjectMetadata> projects = [new ProjectMetadata(alias, dependencyMappingsPath, context)]
        Arguments arguments = new Arguments(stubRunnerOptions, context, unzippedStubsDir.path, alias, projects)
        return new StubRunner(arguments, new StubRegistry(stubRunnerOptions.zookeeperConnectString, client))
    }

    private static class Module {
        final String groupId
        final String artifactId
        final String classifier

        Module(String dependencyMappingsPath, String classifier) {
            String dependencyInPackageNotation = dependencyMappingsPath.replaceAll("/", ".")
            groupId = substringBeforeLast(dependencyInPackageNotation, ".")
            artifactId = substringAfterLast(dependencyInPackageNotation, ".")
            this.classifier = classifier
        }

        Module(String groupId, String artifactId, String classifier) {
            this.groupId = groupId
            this.artifactId = artifactId
            this.classifier = classifier
        }
    }

}
