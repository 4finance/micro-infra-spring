package com.ofg.infrastructure.base

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.ofg.infrastructure.BaseConfiguration
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServiceResolverConfiguration
import com.ofg.infrastructure.discovery.web.MockServerConfiguration
import com.ofg.stub.StubRunning
import groovyx.net.http.HTTPBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = Config)
class CollaboratorStubIntegrationSpec extends MvcWiremockIntegrationSpec {

    @Autowired StubRunning stubRunning
    final String stubName = 'correlator'
    final String stubPath = "com/ofg/$stubName"

    def 'should verify interaction with stub'() {
        given:
            simulatedInteractionWithStub()
        expect:
            stubOf(stubName).verifyThat(expectedRequest())
    }

    private void simulatedInteractionWithStub() {
        URL stubUrl = stubRunning.findStubUrlByRelativePath(stubPath).get()
        def http = new HTTPBuilder(stubUrl)
        http.get(path: "/$stubName")
        http.shutdown()
    }

    private RequestPatternBuilder expectedRequest() {
        def matchingPongEndpoint = new UrlMatchingStrategy()
        matchingPongEndpoint.setUrlPath("/$stubName")
        return new RequestPatternBuilder(RequestMethod.GET, matchingPongEndpoint)
    }

    @Configuration
    @Import([MockServerConfiguration, BaseConfiguration, ServiceResolverConfiguration])
    static class Config {

        @Bean
        ServiceConfigurationResolver serviceConfigurationResolver(
                @Value('${microservice.config.file}') Resource microserviceConfig) {
            return new ServiceConfigurationResolver(microserviceConfig.inputStream.text)
        }

    }

}
