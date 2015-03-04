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

    private final Iterable<StubRunner> stubRunners

    BatchStubRunner(Iterable<StubRunner> stubRunners) {
        this.stubRunners = stubRunners
    }

    @Override
    void runStubs() {
        stubRunners.each {
            it.runStubs()
        }
    }

    @Override
    Optional<URL> findStubUrlByRelativePath(String relativePath) {
        return stubRunners.findResult(Optional.absent()) { StubRunner stubRunner ->
            def optionalUrl = stubRunner.findStubUrlByRelativePath(relativePath)
            if(optionalUrl.present) {
                return optionalUrl
            }
        } as Optional
    }

    @Override
    void close() throws IOException {
        stubRunners.each {
            it.close()
        }
    }
}
