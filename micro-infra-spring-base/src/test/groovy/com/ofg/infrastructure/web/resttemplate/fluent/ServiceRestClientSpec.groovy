package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.ServiceUnavailableException
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestOperations
import spock.lang.Specification

import static org.springframework.http.HttpMethod.GET

class ServiceRestClientSpec extends Specification {

    public static final String COLA_COLLABORATOR_NAME = 'cola'
    RestOperations restOperations = Mock()
    ServiceResolver serviceResolver = Mock()
    
    ServiceRestClient serviceRestClient = new ServiceRestClient(restOperations, serviceResolver)
    
    def "should send a request to provided URL with appending host when calling service"() {
        given:
            String serviceUrl = 'http://localhost:1234'
            String path = 'some/serviceUrl'
            URI expectedUri = new URI("$serviceUrl/$path")
        and:
            serviceResolver.fetchUrl(COLA_COLLABORATOR_NAME) >> serviceUrl
        when:
            serviceRestClient.forService(COLA_COLLABORATOR_NAME).get().onUrl(path).ignoringResponse()
        then:
            1 * restOperations.exchange(expectedUri, GET, _ as HttpEntity, _ as Class)
    }
    
    def "should throw an exception when trying to access an unavailable service"() {
        given:
            serviceResolver.fetchUrl(COLA_COLLABORATOR_NAME) >> { throw new ServiceUnavailableException(COLA_COLLABORATOR_NAME) }
        when:
            serviceRestClient.forService(COLA_COLLABORATOR_NAME).get().onUrl('').ignoringResponse()
        then:
            thrown(ServiceUnavailableException)
    }

    def "should send a request to provided full URL when calling external service"() {
        given:
            String expectedUrlAsString = 'http://localhost:1234/some/url'
            URI expectedUri = new URI(expectedUrlAsString)
        when:
            serviceRestClient.forExternalService().get().onUrl(expectedUrlAsString).ignoringResponse()
        then:
            1 * restOperations.exchange(expectedUri, GET, _ as HttpEntity, _ as Class)
    }
}
