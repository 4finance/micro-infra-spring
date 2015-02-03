package com.ofg.infrastructure.stub

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovyjarjarantlr.collections.List

class Stub {

    private WireMock delegate;

    Stub(String host, int port) {
        this.delegate = new WireMock(host, port)
    }

    void verifyThat(RequestPatternBuilder requestPatternBuilder) {
        delegate.verifyThat(requestPatternBuilder)
    }

    void verifyThat(int count, RequestPatternBuilder requestPatternBuilder) {
        delegate.verifyThat(count, requestPatternBuilder)
    }

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
