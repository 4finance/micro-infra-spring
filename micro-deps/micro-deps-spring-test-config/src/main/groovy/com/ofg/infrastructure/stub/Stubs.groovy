package com.ofg.infrastructure.stub

import com.google.common.base.Supplier
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.StubRunning
import groovy.transform.CompileStatic

@CompileStatic
class Stubs {

    private final StubRunning stubRunning
    private final ServiceConfigurationResolver configurationResolver
    private final Map<String, Stub> mocks = [:].withDefault { createNewStub(it as String) }

    Stubs(ServiceConfigurationResolver configurationResolver, StubRunning stubRunning) {
        this.configurationResolver = configurationResolver
        this.stubRunning = stubRunning
    }

    Stub of(String collaboratorName) {
        return mocks.get(collaboratorName)
    }

    void resetAll() {
        mocks.each { it.value.resetToDefaults() }
    }

    void shutdown() {
        mocks.each { it.value.shutdown() }
    }

    private Stub createNewStub(String collaboratorName) {
        throwIfCollaboratorUnknown(collaboratorName)
        URL stubUrl = findCollaboratorStubUrl(collaboratorName)
        return new Stub(stubUrl.host, stubUrl.port)
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
