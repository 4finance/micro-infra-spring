package com.ofg.infrastructure.base

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import com.ofg.infrastructure.stub.Stub
import com.ofg.infrastructure.stub.Stubs
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import static com.ofg.config.BasicProfiles.TEST

/**
 * Base specification for tests that use Wiremock as HTTP server stub.
 * By extending this specification you gain a bean with {@link HttpMockServer} and a {@link WireMock} 
 * instance that you can stub by using {@link MvcWiremockIntegrationSpec#stubInteraction(com.github.tomakehurst.wiremock.client.MappingBuilder, com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder)}
 * 
 * @see MockServerConfiguration
 * @see WireMock
 * @see HttpMockServer
 * @see MvcIntegrationSpec
 */
@CompileStatic
@ContextConfiguration(classes = [MockServerConfiguration])
@ActiveProfiles(TEST)
abstract class MvcWiremockIntegrationSpec extends MvcIntegrationSpec {

    @Autowired protected HttpMockServer httpMockServer
    @Autowired protected Stubs stubs
    protected WireMock wireMock
    
    void setup() {
        wireMock = new WireMock(wireMockHost, wireMockPort)
        wireMock.resetToDefaultMappings()
    }

    protected String getWireMockUrl() {
        return "http://$wireMockHost:$wireMockPort"
    }

    protected String getWireMockHost() {
        return 'localhost'
    }

    protected int getWireMockPort() {
        return httpMockServer.port()
    }

    protected void stubInteraction(MappingBuilder mapping, ResponseDefinitionBuilder response) {
        wireMock.register(mapping.willReturn(response))
    }

    protected Stub stubOf(String collaboratorName) {
        return stubs.of(collaboratorName)
    }

    protected void cleanup() {
        stubs.resetAll()
    }

}
