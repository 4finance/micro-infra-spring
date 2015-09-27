package com.ofg.stub

import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
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
    private final ServiceConfigurationResolver serviceConfigurationResolver

    BatchStubRunnerFactory(StubRunnerOptions stubRunnerOptions, ServiceConfigurationResolver serviceConfigurationResolver) {
        this.stubRunnerOptions = stubRunnerOptions
        this.serviceConfigurationResolver = serviceConfigurationResolver
    }

    BatchStubRunner buildBatchStubRunner() {
        List<Optional<StubRunner>> stubRunners
        StubRunnerFactory stubRunnerFactory = new StubRunnerFactory(stubRunnerOptions, serviceConfigurationResolver)
        if (!stubRunnerOptions.useMicroserviceDefinitions) {
            stubRunners = stubRunnerFactory.createStubsFromStubsModule()
        } else {
            stubRunners = stubRunnerFactory.createStubsFromServiceConfiguration()
        }
        return new BatchStubRunner(Optional.presentInstances(stubRunners))
    }

}
