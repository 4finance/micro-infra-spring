package com.ofg.infrastructure.stub

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovy.transform.CompileStatic
import groovyjarjarantlr.collections.List

@CompileStatic
class Stub {

    private final WireMock delegate
    final String host
    final int port

    Stub(String host, int port) {
        this.delegate = new WireMock(host, port)
        this.host = host
        this.port = port
    }

    /**
     * Verifies that a single call to the mock with given expected request criteria took place.
     *
     * @param requestPatternBuilder expected request criteria
     */
    void verifyThat(RequestPatternBuilder requestPatternBuilder) {
        delegate.verifyThat(requestPatternBuilder)
    }

    /**
     * Verifies that multiple calls ({@code count} times) to the mock with given expected request criteria took place.
     *
     * @param count expectation on how many times the interaction with mock took place
     * @param requestPatternBuilder expected request criteria
     */
    void verifyThat(int count, RequestPatternBuilder requestPatternBuilder) {
        delegate.verifyThat(count, requestPatternBuilder)
    }

    /**
     * Resets all interactions and mappings for this stub to the starting point,
     * i.e. with no interactions registered and mappings loaded from stub mappings definitions.
     */
    void resetToDefaults() {
        def stubMappings = delegate.allStubMappings()
        delegate.resetToDefaultMappings()
        reRegister(delegate, stubMappings)
    }

    private List reRegister(WireMock mock, ListStubMappingsResult stubMappings) {
        (stubMappings.mappings as List).each { mock.register(it as StubMapping) }
    }

    void shutdown() {
        delegate.shutdown()
    }

}
