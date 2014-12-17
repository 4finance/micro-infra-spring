package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
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
    
}
