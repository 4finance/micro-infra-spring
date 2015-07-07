package com.ofg.infrastructure.base

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.ofg.infrastructure.BaseConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
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
    static final String STUB_NAME = 'correlator'
    static final ServiceAlias STUB_ALIAS = new ServiceAlias(STUB_NAME)
    static final String STUB_PATH = "com/ofg/$STUB_NAME"

    def 'should verify interaction with stub'() {
        given:
            simulatedInteractionWithStub()
        expect:
            stubOf(STUB_ALIAS).verifyThat(expectedRequest())
    }

    private void simulatedInteractionWithStub() {
        URL stubUrl = stubRunning.findStubUrlByRelativePath(STUB_PATH).get()
        def http = new HTTPBuilder(stubUrl)
        http.get(path: "/$STUB_NAME")
        http.shutdown()
    }

    private RequestPatternBuilder expectedRequest() {
        def matchingPongEndpoint = new UrlMatchingStrategy()
        matchingPongEndpoint.setUrlPath("/$STUB_NAME")
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
