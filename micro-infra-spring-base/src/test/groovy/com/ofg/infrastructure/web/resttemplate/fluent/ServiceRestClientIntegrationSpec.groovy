package com.ofg.infrastructure.web.resttemplate.fluent

import com.google.common.base.Optional
import com.google.common.util.concurrent.ListenableFuture
import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.ServiceResolver
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.IgnoreRest
import spock.lang.Shared

@ActiveProfiles(['stub', BasicProfiles.TEST])
@ContextConfiguration(classes = [BaseConfiguration, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class ServiceRestClientIntegrationSpec extends MvcWiremockIntegrationSpec {

    private static final String COLLABORATOR_NAME = 'foo-bar'
    private static final String PATH = '/pl/foobar'
    private static final String CONTEXT_SPECIFIC_FOOBAR = 'foobar Poland'

    @Shared @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', 'localhost:2183');

    @Autowired ServiceRestClient serviceRestClient

    @Autowired ServiceResolver serviceResolver

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

    def "should properly construct parameterized external URL"() {
        given:
            ServicePath path = serviceResolver.resolveAlias(new ServiceAlias(COLLABORATOR_NAME))
            URI uri = serviceResolver.getUri(path).get()
        when:
            String result = serviceRestClient
                    .forExternalService()
                    .get()
                    .onUrlFromTemplate(uri.toString() + '/pl/{name}')
                    .withVariables('foobar')
                    .andExecuteFor()
                    .anObject()
                    .ofType(String)
        then:
            result == CONTEXT_SPECIFIC_FOOBAR
    }

    def "should properly construct external URL from template"() {
        given:
            ServicePath path = serviceResolver.resolveAlias(new ServiceAlias(COLLABORATOR_NAME))
            String uri = serviceResolver.getUri(path).get().toString()
        when:
            String result = serviceRestClient
                    .forExternalService()
                    .get()
                    .onUrlFromTemplate('{uri}/pl/{name}')
                    .withVariables(uri, 'foobar')
                    .andExecuteFor()
                    .anObject()
                    .ofType(String)
        then:
            result == CONTEXT_SPECIFIC_FOOBAR
    }

    def "should properly construct external URL from GString"() {
        given:
            URI uri = uriOf(COLLABORATOR_NAME)
            String name = 'foobar'
        when:
            String result = serviceRestClient
                    .forExternalService()
                    .get()
                    .onUrl("${uri.toString()}/pl/$name")
                    .andExecuteFor()
                    .anObject()
                    .ofType(String)
        then:
            result == CONTEXT_SPECIFIC_FOOBAR
    }

    private URI uriOf(String collaboratorAlias) {
        ServicePath path = serviceResolver.resolveAlias(new ServiceAlias(COLLABORATOR_NAME))
        return serviceResolver.getUri(path).get()
    }

}
