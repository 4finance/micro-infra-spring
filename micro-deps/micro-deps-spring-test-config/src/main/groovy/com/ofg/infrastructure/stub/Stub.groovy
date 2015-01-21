package com.ofg.infrastructure.stub

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.google.common.base.Supplier
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.StubRunning
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovyjarjarantlr.collections.List

@CompileStatic
@TypeChecked
class Stub {

    private final StubRunning stubRunning
    private final ServiceConfigurationResolver configurationResolver
    private final Map<String, WireMock> mocks = [:].withDefault { createNewMock(it as String) }

    Stub(ServiceConfigurationResolver configurationResolver, StubRunning stubRunning) {
        this.configurationResolver = configurationResolver
        this.stubRunning = stubRunning
    }

    WireMock of(String collaboratorName) {
        return mocks.get(collaboratorName)
    }

    void resetAll() {
        mocks.each {
            def allStubMappings = it.value.allStubMappings()
            it.value.resetToDefaultMappings()
            reRegisterMappings(it.value, allStubMappings)
        }
    }

    private void reRegisterMappings(WireMock stub, ListStubMappingsResult stubMappings) {
        (stubMappings.mappings as List).each { stub.register(it as StubMapping) }
    }

    void shutdown() {
        mocks.each { it.value.shutdown() }
    }

    private WireMock createNewMock(String collaboratorName) {
        throwIfCollaboratorUnknown(collaboratorName)
        URL stubUrl = findCollaboratorStubUrl(collaboratorName)
        return new WireMock(stubUrl.host, stubUrl.port)
    }

    private void throwIfCollaboratorUnknown(String collaboratorName) {
        if (isUnknown(collaboratorName)) {
            throw new NoSuchElementException("Unknown collaborator name: $collaboratorName")
        }
    }

    private URL findCollaboratorStubUrl(String collaboratorName) {
        String collaboratorPath = configurationResolver.dependencies[collaboratorName]['path']
        def optionalStubUrl = stubRunning.findStubUrlByRelativePath(collaboratorPath)
        return optionalStubUrl.or( { throw new MissingStubException(collaboratorName) } as Supplier )
    }

    private boolean isUnknown(String collaboratorName) {
        !configurationResolver.dependencies.containsKey(collaboratorName)
    }

}

@CompileStatic
class MissingStubException extends RuntimeException {

    MissingStubException(String collaboratorName) {
        super("Missing stub for collaborator name: $collaboratorName")
    }

}
