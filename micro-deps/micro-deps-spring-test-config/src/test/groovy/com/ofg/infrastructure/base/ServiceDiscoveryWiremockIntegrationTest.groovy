package com.ofg.infrastructure.base

import com.ofg.infrastructure.BaseConfiguration
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.ServiceResolverConfiguration
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.jayway.restassured.RestAssured.get
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet
import static org.springframework.http.HttpStatus.OK

@ContextConfiguration(classes = [MockServerConfiguration, BaseConfiguration, ServiceResolverConfiguration])
class ServiceDiscoveryWiremockIntegrationTest extends MvcWiremockIntegrationTest {
   
    @Autowired ServiceResolver serviceResolver
    @Autowired ServiceConfigurationResolver serviceConfigurationResolver

    @Before
    void setup() {
        super.setup()
    }

    @Test
    void "should bind zookeeper stub's address with wiremock"() {
        stubInteraction(wireMockGet('/correlator'), aResponse().withStatus(OK.value()))

        get("http://localhost:${httpMockServer.port()}/correlator").then().statusCode(OK.value())
    }
    
}
