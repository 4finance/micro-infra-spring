package com.ofg.infrastructure.base

import com.google.common.base.Optional as GuavaOptional
import com.ofg.infrastructure.BaseConfiguration
import com.ofg.infrastructure.discovery.ServiceDiscoveryStubbingConfiguration
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.jayway.restassured.RestAssured.get
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet
import static org.springframework.http.HttpStatus.OK

@ContextConfiguration(classes = [MockServerConfiguration, BaseConfiguration, ServiceDiscoveryStubbingConfiguration])
class ServiceDiscoveryWiremockIntegrationSpec extends MvcWiremockIntegrationSpec {
   
    @Autowired ServiceResolver serviceResolver
    
    def 'should inject wiremock properties'() {
        expect:
            wiremockUrl != null        
    }
    
    def "should bind zookeeper stub's address with wiremock"() {
        given:
            stubInteraction(wireMockGet('/collerator'), aResponse().withStatus(OK.value()))
            GuavaOptional<String> resolvedDependency = serviceResolver.getUrl('collerator')
        expect:
            resolvedDependency.isPresent()
        and:
            String microserviceUrl = resolvedDependency.get()
            get(microserviceUrl).then().statusCode(OK.value())
    }
    
    def 'should reset address stubbing'() {
        given:
            stubbedServiceResolver.resetDependencies()
        when:
            GuavaOptional<String> resolvedDependency = serviceResolver.getUrl('collerator')
        then:
            !resolvedDependency.isPresent()
    }
    
    def 'should add a single stub'() {
        given:
            stubbedServiceResolver.resetDependencies()
            String stubbedDepName = 'teststub'
            String stubbedUrl = 'http://someAddress:3030'
            stubbedServiceResolver.stubDependency(stubbedDepName, stubbedUrl)
        when:
            GuavaOptional<String> resolvedDependency = serviceResolver.getUrl(stubbedDepName)
        then:
            resolvedDependency.isPresent()
            resolvedDependency.get() == stubbedUrl
    }
    
}
