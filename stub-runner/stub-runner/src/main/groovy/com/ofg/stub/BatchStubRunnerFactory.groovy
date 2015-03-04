package com.ofg.stub

import com.google.common.base.Optional
import groovy.transform.CompileStatic

/**
 * Manages lifecycle of multiple {@link StubRunner} instances.
 *
 * @see StubRunner
 * @see BatchStubRunner
 */
@CompileStatic
class BatchStubRunnerFactory {

    private final StubRunnerOptions stubRunnerOptions
    private final Collaborators dependencies

    BatchStubRunnerFactory(StubRunnerOptions stubRunnerOptions, Collaborators dependencies) {
        this.stubRunnerOptions = stubRunnerOptions
        this.dependencies = dependencies
    }

    BatchStubRunner buildBatchStubRunner() {
        List<Optional<StubRunner>> stubRunners
        StubRunnerFactory stubRunnerFactory = new StubRunnerFactory(stubRunnerOptions, dependencies)
        if (!stubRunnerOptions.useMicroserviceDefinitions) {
            stubRunners = stubRunnerFactory.createStubsFromStubsModule()
        } else {
            stubRunners = stubRunnerFactory.createStubsFromServiceConfiguration()
        }
        return new BatchStubRunner(Optional.presentInstances(stubRunners))
    }

}
