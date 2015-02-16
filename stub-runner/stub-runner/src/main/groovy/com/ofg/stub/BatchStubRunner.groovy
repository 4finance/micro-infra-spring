package com.ofg.stub

import com.google.common.base.Optional
import groovy.transform.CompileStatic

/**
 * Manages lifecycle of multiple {@link StubRunner} instances.
 *
 * @see StubRunner
 */
@CompileStatic
class BatchStubRunner implements StubRunning {

    private final List<StubRunner> stubRunners

    BatchStubRunner(List<StubRunner> stubRunners) {
        this.stubRunners = stubRunners
    }

    @Override
    void runStubs() {
        stubRunners*.runStubs()
    }

    @Override
    Optional<URL> findStubUrlByRelativePath(String relativePath) {
        return stubRunners.findResult(Optional.absent()) {
            def optionalUrl = it.findStubUrlByRelativePath(relativePath)
            if(optionalUrl.present) {
                return optionalUrl
            }
        } as Optional
    }

    @Override
    void close() throws IOException {
        stubRunners*.close()
    }
}
