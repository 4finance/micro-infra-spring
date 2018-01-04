package com.ofg.infrastructure.base

import com.ofg.infrastructure.BaseConfiguration
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.ServiceResolverConfiguration
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet
import static io.restassured.RestAssured.get
import static org.springframework.http.HttpStatus.OK

@WebAppConfiguration
@ContextConfiguration(classes = [MockServerConfiguration, BaseConfiguration, ServiceResolverConfiguration])
class ServiceDiscoveryWiremockIntegrationSpec extends MvcWiremockIntegrationSpec {

    @Autowired ServiceResolver serviceResolver
    @Autowired ServiceConfigurationResolver serviceConfigurationResolver

    def "should bind zookeeper stub's address with wiremock"() {
        given:
            stubInteraction(wireMockGet('/correlator'), aResponse().withStatus(OK.value()))
        expect:
            get("http://localhost:${httpMockServer.port()}/correlator").then().statusCode(OK.value())
    }

}
