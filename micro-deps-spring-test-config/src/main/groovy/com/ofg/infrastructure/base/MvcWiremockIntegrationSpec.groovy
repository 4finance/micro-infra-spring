package com.ofg.infrastructure.base
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ContextConfiguration

@TypeChecked
@ContextConfiguration(classes = [MockServerConfiguration])
class MvcWiremockIntegrationSpec extends MvcIntegrationSpec {

    WireMock colaWireMock
    @Autowired HttpMockServer httpMockServer    
    @Value('${wiremock.url:localhost}') String wiremockUrl
    
    void setup() {
        colaWireMock = new WireMock(wiremockUrl, httpMockServer.port())
        colaWireMock.resetMappings()
    }

    protected void stubInteraction(MappingBuilder mapping, ResponseDefinitionBuilder response) {
        colaWireMock.register(mapping.willReturn(response))
    }
    
    
    
}
