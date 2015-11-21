package com.ofg.infrastructure.web.resttemplate.fluent
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.google.common.util.concurrent.UncheckedTimeoutException
import com.ofg.config.BasicProfiles
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.base.ServiceDiscoveryStubbingApplicationConfiguration
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.hystrix.CircuitBreakers
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ProvideSystemProperty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.SocketUtils
import org.springframework.web.client.ResourceAccessException
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockGet

@ActiveProfiles(['stub', BasicProfiles.TEST])
@ContextConfiguration(classes = [BaseConfiguration, ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
class ServiceRestClientIntegrationSpec extends MvcWiremockIntegrationSpec {

    private static final ServiceAlias COLLABORATOR_ALIAS = new ServiceAlias('foo-bar')
    private static final String PATH = '/pl/foobar'
    private static final String CONTEXT_SPECIFIC_FOOBAR = 'foobar Poland'

    @Value('${rest.client.readTimeout}')
    int readTimeoutMillis

    public static final Integer FREE_PORT = SocketUtils.findAvailableTcpPort()

    @Shared
    @ClassRule
    public ProvideSystemProperty resolverUrlPropertyIsSet = new ProvideSystemProperty('service.resolver.url', "localhost:$FREE_PORT");

    @Autowired ServiceRestClient serviceRestClient

    @Autowired ServiceResolver serviceResolver

    def "should send a request to provided URL with appending host when calling service"() {
        when:
            ResponseEntity<String> result = serviceRestClient
                    .forService(COLLABORATOR_ALIAS)
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
            )
        when:
            serviceRestClient.forExternalService()
                    .get()
                    .onUrl("http://localhost:${httpMockServer.port()}/delayed")
                    .andExecuteFor()
                    .anObject().ofType(String)
        then:
            def exception = thrown(ResourceAccessException)
            exception.cause instanceof SocketTimeoutException
            exception.message.contains('Read timed out')
    }

    def "should be interrupted and throw UncheckedTimeoutException when service does not respond"() {
        given:
            Integer fixedResponseDelayMilliseconds = readTimeoutMillis / 2
            Integer circuitBreakersTimeoutInMillis = fixedResponseDelayMilliseconds / 2
            String delayedPath = "/delayed-for-interruption"
            stubInteraction(wireMockGet(delayedPath), aResponse().withFixedDelay(fixedResponseDelayMilliseconds))
        when:
            serviceRestClient.forExternalService()
                    .get()
                    .withCircuitBreaker(CircuitBreakers.anyWithTimeout(circuitBreakersTimeoutInMillis))
                    .onUrl("http://localhost:${httpMockServer.port()}${delayedPath}")
                    .andExecuteFor()
                    .anObject().ofType(String.class);
        then:
            thrown(UncheckedTimeoutException)
    }

    def 'should log HTTP response using Logback'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender.class))
        when:
            ResponseEntity<String> result = serviceRestClient
                    .forService(COLLABORATOR_ALIAS)
                    .get()
                    .onUrl(PATH)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(String)
        then:
            (1.._) * mockAppender.doAppend({ILoggingEvent e ->
                e.formattedMessage.contains(CONTEXT_SPECIFIC_FOOBAR)
            })
        and:
            result.body == CONTEXT_SPECIFIC_FOOBAR
        cleanup:
            removeAppender(mockAppender)
    }

    Appender insertAppender(Appender appender) {
        root().addAppender(appender);
        return appender
    }

    void removeAppender(Appender appender) {
        root().detachAppender(appender)
    }

    private Logger root() {
        return (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
    }

    def "should properly construct parameterized external URL"() {
        given:
            URI uri = serviceResolver.fetchUri(COLLABORATOR_ALIAS)
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
            String uri = serviceResolver.fetchUri(COLLABORATOR_ALIAS).toString()
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
            URI uri = serviceResolver.fetchUri(COLLABORATOR_ALIAS)
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
}
