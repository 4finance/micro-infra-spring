package com.ofg.infrastructure.stub

import com.github.tomakehurst.wiremock.client.RemoteMappingBuilder
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.VerificationException
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.google.common.base.Optional
import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.stub.StubRunning
import groovyx.net.http.HTTPBuilder
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet

class StubSpec extends Specification {

    static final ServiceAlias UNKOWN_COLLABORATOR = new ServiceAlias('unknown')
    static final ServiceAlias PING = new ServiceAlias('ping')
    static final ServiceAlias PONG = new ServiceAlias('pong')
    static final ServicePath UNKNOWN_PING_PATH = new ServicePath('/com/ofg/unknown/ping')
    static final ServicePath KNOWN_PONG_PATH = new ServicePath('/com/ofg/pong')
    static final String PONG_ENDPOINT = '/pong'
    static final int MOCK_PORT = 8993

    @AutoCleanup('shutdownServer') HttpMockServer mockServer
    @AutoCleanup('shutdown') WireMock wireMock
    StubRunning stubRunning
    ServiceConfigurationResolver configurationResolver

    def setup() {
        stubRunningWithPredefinedPongPath()
        configurationResolverWithPingPongDependencies()

        mockServer = new HttpMockServer(MOCK_PORT)
        mockServer.start()

        wireMock = new WireMock('localhost', mockServer.port())
        wireMock.resetToDefaultMappings()
    }

    def 'should successfully verify interaction with stub'() {
        given:
            predefinedPongInteraction()
            simulatedSinglePongInteraction()
        when:
            Stubs stub = new Stubs(configurationResolver, stubRunning)
            stub.of(PONG).verifyThat(expectedRequest())
        then:
            noExceptionThrown()
        cleanup:
            stub.shutdown()
    }

    def 'should throw verification exception if no verification happened'() {
        given:
            predefinedPongInteraction()
        when:
            Stubs stub = new Stubs(configurationResolver, stubRunning)
            stub.of(PONG).verifyThat(expectedRequest())
        then:
            thrown(VerificationException)
        cleanup:
            stub.shutdown()
    }

    def 'should successfully verify multiple interactions with stub'() {
        given:
            predefinedPongInteraction()
            multiplePongInteractions(3)
        when:
            Stubs stub = new Stubs(configurationResolver, stubRunning)
            stub.of(PONG).verifyThat(3, expectedRequest())
        then:
            noExceptionThrown()
        cleanup:
            stub.shutdown()
    }

    @Unroll('should throw exception when there were #actualCount interactions while #verificationCount were expected')
    def 'should throw exception on mismatch between actual and expected interactions count'() {
        given:
            predefinedPongInteraction()
            multiplePongInteractions(3)
        when:
            Stubs stub = new Stubs(configurationResolver, stubRunning)
            stub.of(PONG).verifyThat(2, expectedRequest())
        then:
            thrown(VerificationException)
        cleanup:
            stub.shutdown()
        where:
            actualCount | verificationCount
            3           | 5
            5           | 4
    }

    def 'should throw exception for unknown collaborator alias'() {
        given:
            Stubs stub = new Stubs(configurationResolver, stubRunning)
        when:
            stub.of(UNKOWN_COLLABORATOR)
        then:
            def ex = thrown(UnknownCollaboratorException)
            ex.message == "Could not resolve service with alias: $UNKOWN_COLLABORATOR"
        cleanup:
            stub.shutdown()
    }

    def 'should throw exception for missing stub URL of well-known collaborator alias'() {
        given:
            Stubs stub = new Stubs(configurationResolver, stubRunning)
        when:
            stub.of(PING)
        then:
            def ex = thrown(MissingStubException)
            ex.message == "Could not find stub with alias: $PING"
        cleanup:
            stub.shutdown()
    }

    private void configurationResolverWithPingPongDependencies() {
        configurationResolver = Mock(ServiceConfigurationResolver)
        MicroserviceConfiguration.Dependency ping = new MicroserviceConfiguration.Dependency(PING, UNKNOWN_PING_PATH)
        MicroserviceConfiguration.Dependency pong = new MicroserviceConfiguration.Dependency(PONG, KNOWN_PONG_PATH)
        configurationResolver.getDependency(PING) >> ping
        configurationResolver.getDependency(PONG) >> pong
        configurationResolver.dependencies >> [ping, pong]
    }

    private void stubRunningWithPredefinedPongPath() {
        stubRunning = Mock(StubRunning)
        stubRunning.findStubUrlByRelativePath(KNOWN_PONG_PATH.path) >> Optional.of(new URL("http://localhost:$MOCK_PORT"))
        stubRunning.findStubUrlByRelativePath(_ as String) >> Optional.absent()
    }

    private void simulatedSinglePongInteraction() {
        def http = new HTTPBuilder("http://localhost:$MOCK_PORT")
        http.get(path: PONG_ENDPOINT)
        http.shutdown()
    }

    private void multiplePongInteractions(int interactionsCount) {
        interactionsCount.times { simulatedSinglePongInteraction() }
    }

    private predefinedPongInteraction() {
        stubInteraction(wireMockGet(PONG_ENDPOINT), aResponse().withStatus(200))
    }

    private RequestPatternBuilder expectedRequest() {
        def matchingPongEndpoint = new UrlMatchingStrategy()
        matchingPongEndpoint.setUrlPath(PONG_ENDPOINT)
        return new RequestPatternBuilder(RequestMethod.GET, matchingPongEndpoint)
    }

    private void stubInteraction(RemoteMappingBuilder mapping, ResponseDefinitionBuilder response) {
        wireMock.register(mapping.willReturn(response))
    }

}
