package com.ofg.infrastructure.base
import com.google.common.base.Optional as GuavaOptional
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
        stubbedServiceResolver.stubDependenciesFrom(serviceConfigurationResolver)
    }

    @Test
    void 'should inject wiremock properties'() {
        wiremockUrl != null
    }

    @Test
    void "should bind zookeeper stub's address with wiremock"() {
        stubInteraction(wireMockGet('/collerator'), aResponse().withStatus(OK.value()))

        GuavaOptional<String> resolvedDependency = serviceResolver.getUrl('collerator')

        assert resolvedDependency.isPresent()
        String microserviceUrl = resolvedDependency.get()
        get(microserviceUrl).then().statusCode(OK.value())
    }

    @Test
    void 'should reset address stubbing'() {
        stubbedServiceResolver.resetDependencies()

        GuavaOptional<String> resolvedDependency = serviceResolver.getUrl('collerator')

        assert !resolvedDependency.isPresent()
    }

    @Test
    void 'should add a single stub'() {
        stubbedServiceResolver.resetDependencies()
        String stubbedDepName = 'teststub'
        String stubbedUrl = 'http://someAddress:3030'
        stubbedServiceResolver.stubDependency(stubbedDepName, stubbedUrl)

        GuavaOptional<String> resolvedDependency = serviceResolver.getUrl(stubbedDepName)

        assert resolvedDependency.isPresent()
        assert resolvedDependency.get() == stubbedUrl
    }
    
}
