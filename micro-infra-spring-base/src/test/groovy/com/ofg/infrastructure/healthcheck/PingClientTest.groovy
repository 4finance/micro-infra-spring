package com.ofg.infrastructure.healthcheck

import com.google.common.base.Optional
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.discovery.ServiceResolverConfiguration
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet

@ContextConfiguration(classes = [BaseConfiguration, MockServerConfiguration, ServiceResolverConfiguration,
        CollaboratorsConfiguration, ServiceRestClientConfiguration], loader = SpringApplicationContextLoader)
class PingClientTest extends MvcWiremockIntegrationSpec {

    @Autowired
    PingClient pingClient

    def 'should return collaborators #expected response when /collaborators return HTTP #code and body #body'() {
        given:
            stubInteraction(wireMockGet('/collaborators'),
                    aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withStatus(code)
                            .withBody(body))
        when:
            Optional<Map> collaborators = pingClient.checkCollaborators(hostAndPort())
        then:
            collaborators == expected
        where:
            code | body                                          || expected
            200  | ''                                            || Optional.absent()
            200  | '{}'                                          || Optional.of([:])
            200  | '{"/com/ofg/foo": {"http://foo:8080": "UP"}}' || Optional.of([("/com/ofg/foo"): [("http://foo:8080"): "UP"]])
            404  | ''                                            || Optional.absent()
    }

    def "ping returning #code and body '#body' should have result #result"() {
        given:
            stubInteraction(wireMockGet('/ping'), aResponse().withStatus(code).withBody(body))
        when:
            Optional<String> ping = pingClient.ping(hostAndPort())
        then:
            ping.present == result
        where:
            code | body || result
            200  | 'OK' || true
            200  | ''   || true
            404  | 'OK' || false
            404  | ''   || false
            503  | ''   || false
    }

    URI hostAndPort() {
        return "http://localhost:${httpMockServer.port()}".toURI()
    }

}
