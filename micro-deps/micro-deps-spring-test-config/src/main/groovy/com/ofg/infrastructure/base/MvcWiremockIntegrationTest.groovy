package com.ofg.infrastructure.base

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import groovy.transform.CompileStatic
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

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
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = [MockServerConfiguration])
@ActiveProfiles(TEST)
abstract class MvcWiremockIntegrationTest extends MvcIntegrationTest {

    WireMock wireMock
    @Autowired HttpMockServer httpMockServer    
    @Value('${wiremock.url:localhost}') String wiremockUrl

    @Before
    void setup() {
        super.setup()
        wireMock = new WireMock(wiremockUrl, httpMockServer.port())
        wireMock.resetToDefaultMappings()
    }

    protected void stubInteraction(MappingBuilder mapping, ResponseDefinitionBuilder response) {
        wireMock.register(mapping.willReturn(response))
    }
    
    
    
}
