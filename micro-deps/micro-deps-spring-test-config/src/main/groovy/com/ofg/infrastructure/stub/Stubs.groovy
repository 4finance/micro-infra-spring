package com.ofg.infrastructure.stub

import com.google.common.base.Supplier
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.stub.StubRunning
import groovy.transform.CompileStatic

@CompileStatic
class Stubs {

    private final StubRunning stubRunning
    private final ServiceConfigurationResolver configurationResolver
    private final Map<ServiceAlias, Stub> mocks = [:].withDefault { createNewStub(it as ServiceAlias) }

    Stubs(ServiceConfigurationResolver configurationResolver, StubRunning stubRunning) {
        this.configurationResolver = configurationResolver
        this.stubRunning = stubRunning
    }

    /**
     *
     * @deprecated since 0.9.1, use {@link #of(ServiceAlias collaboratorAlias)} instead
     */
    @Deprecated
    Stub of(String collaboratorName) {
        return of(new ServiceAlias(collaboratorName))
    }

    Stub of(ServiceAlias serviceAlias) {
        return mocks[serviceAlias]
    }

    void resetAll() {
        mocks.each { it.value.resetToDefaults() }
    }

    void shutdown() {
        mocks.each { it.value.shutdown() }
    }

    private Stub createNewStub(ServiceAlias serviceAlias) {
        throwIfCollaboratorUnknown(serviceAlias)
        URL stubUrl = findCollaboratorStubUrl(serviceAlias)
        return new Stub(stubUrl.host, stubUrl.port)
    }

    private void throwIfCollaboratorUnknown(ServiceAlias serviceAlias) {
        if (isUnknown(serviceAlias)) {
            throw new UnknownCollaboratorException(serviceAlias)
        }
    }

    private URL findCollaboratorStubUrl(ServiceAlias serviceAlias) {
        String collaboratorPath = configurationResolver.getDependency(serviceAlias)?.servicePath?.path
        def optionalStubUrl = stubRunning.findStubUrlByRelativePath(collaboratorPath)
        return optionalStubUrl.or( { throw new MissingStubException(serviceAlias) } as Supplier )
    }

    private boolean isUnknown(ServiceAlias serviceAlias) {
        return !configurationResolver.dependencies.find { it.serviceAlias == serviceAlias }
    }
}

@CompileStatic
class MissingStubException extends RuntimeException {

    MissingStubException(serviceAlias) {
        super("Could not find stub with alias: $serviceAlias")
    }
}

@CompileStatic
class UnknownCollaboratorException extends RuntimeException {

    UnknownCollaboratorException(ServiceAlias serviceAlias) {
        super("Could not resolve service with alias: $serviceAlias")
    }
}