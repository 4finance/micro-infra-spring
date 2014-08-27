package com.ofg.infrastructure.web.resttemplate.fluent
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.discovery.ServiceDiscoveryStubbingConfiguration
import com.ofg.infrastructure.healthcheck.HealthCheckConfiguration
import com.ofg.infrastructure.web.config.WebInfrastructureConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(classes = [BaseConfiguration, ServiceDiscoveryStubbingConfiguration, WebInfrastructureConfiguration, HealthCheckConfiguration], loader = SpringApplicationContextLoader)
class ServiceRestClientIntegrationSpec extends MvcWiremockIntegrationSpec {

    public static final String COLLABORATOR_NAME = 'users'
    public static final String PATH = 'some/url'
    public static final String FULL_COLLABORATOR_WIREMOCK_PATH = "/$COLLABORATOR_NAME/$PATH"
    public static final String RESPONSE_XML = '''<response>body</response>'''
    
    @Autowired ServiceRestClient serviceRestClient
    
    def "should send a request to provided URL with appending host when calling service"() {
        given:
            stubInteraction(get(urlEqualTo(FULL_COLLABORATOR_WIREMOCK_PATH)), aResponse().withBody(RESPONSE_XML).withStatus(200))
        when:
            ResponseEntity<String> result = serviceRestClient.forService(COLLABORATOR_NAME).get().onUrl(PATH).withHeaders().contentTypeXml().andExecuteFor().aResponseEntity().ofType(String)
        then:
            result.body == RESPONSE_XML
    }
    
}
