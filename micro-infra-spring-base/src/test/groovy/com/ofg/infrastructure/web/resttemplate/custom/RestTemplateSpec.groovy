package com.ofg.infrastructure.web.resttemplate.custom
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.springframework.http.HttpStatus.NOT_FOUND

class RestTemplateSpec extends Specification {

    @Shared
    @AutoCleanup("stop")
    private WireMockServer wireMockServer = new WireMockServer(0)

    @Shared
    private int port

    def setupSpec() {
        wireMockServer.start()
        port = wireMockServer.port()
        WireMock.configureFor("localhost", port)
    }

    def cleanup() {
        WireMock.reset()
    }

    def 'should handle exception thrown in getBody() during error handling when body empty'() {
        given:
            def template = new RestTemplate()
        and:
            returns404withNoBody("/bad")

        when:
            template.getForObject("http://localhost:$port/bad", String)

        then:
            def ex = thrown(ResponseException)
            ex.httpStatus == NOT_FOUND
            ex.body == ""
    }

    def 'should include 404 body'() {
        given:
            def template = new RestTemplate()
        and:
            returns404withBody("/bad2", "NOT_FOUND_SORRY")

        when:
            template.getForObject("http://localhost:$port/bad2", String)

        then:
            def ex = thrown(ResponseException)
            ex.httpStatus == NOT_FOUND
            ex.body == "NOT_FOUND_SORRY"
    }

    private static returns404withNoBody(String address) {
        stubFor(get(urlEqualTo(address))
                .willReturn(
                aResponse()
                        .withStatus(NOT_FOUND.value())))
    }

    private static returns404withBody(String address, String body) {
        stubFor(get(urlEqualTo(address))
                .willReturn(
                aResponse()
                        .withStatus(NOT_FOUND.value())
                        .withBody(body)))
    }

}
