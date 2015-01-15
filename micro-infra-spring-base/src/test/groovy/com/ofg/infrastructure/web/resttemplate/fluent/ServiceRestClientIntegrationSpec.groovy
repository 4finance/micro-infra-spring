package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.ResourceAccessException
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet

@ActiveProfiles(['stub', BasicProfiles.TEST])
@ContextConfiguration(classes = [BaseConfiguration, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class ServiceRestClientIntegrationSpec extends MvcWiremockIntegrationSpec {

    private static final String COLLABORATOR_NAME = 'foo-bar'
    private static final String PATH = '/pl/foobar'
    private static final String CONTEXT_SPECIFIC_FOOBAR = 'foobar Poland'

    @Value('${microservice.restclient.readTimeout}')
    int readTimeoutMillis

    @Shared
    @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2183');

    @Autowired
    ServiceRestClient serviceRestClient

    def "should send a request to provided URL with appending host when calling service"() {
        when:
            ResponseEntity<String> result = serviceRestClient
                    .forService(COLLABORATOR_NAME)
                    .get()
                    .onUrl(PATH)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(String)
        then:
            result.body == CONTEXT_SPECIFIC_FOOBAR
    }

    def "should throw an exception when service does not respond"() {
        given:
            stubInteraction(wireMockGet('/delayed'), aResponse()
                    .withFixedDelay(readTimeoutMillis * 2)
                    .withBody("THIS SHOULD TIME OUT")
            )
        when:
            serviceRestClient.forExternalService()
                    .get()
                    .onUrl("http://$wiremockUrl:${httpMockServer.port()}/delayed")
                    .andExecuteFor()
                    .anObject().ofType(String)
        then:
            def exception = thrown(ResourceAccessException)
            exception.cause instanceof SocketTimeoutException
            exception.message.contains('Read timed out')

    }

}
