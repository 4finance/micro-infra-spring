package com.ofg.infrastructure.web.resttemplate.fluent

import com.google.common.util.concurrent.ListenableFuture
import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.ofg.infrastructure.discovery.ServiceAlias
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.discovery.ServiceUnavailableException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.HEAD
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpMethod.PUT

class ServiceRestClientSpec extends Specification {

    public static final String COLA_COLLABORATOR_NAME = 'cola'
    public static final String SOME_SERVICE_URL = 'http://localhost:1234'
    RestOperations restOperations = Mock()
    ServiceResolver serviceResolver = Mock()
    ServiceConfigurationResolver configurationResolver = Mock()
    @AutoCleanup("shutdownNow")
    @Shared
    ScheduledExecutorService pool = Executors.newScheduledThreadPool(1)
    ServiceRestClient serviceRestClient = new ServiceRestClient(restOperations, serviceResolver, configurationResolver)

    def setup() {
        configurationResolver.getDependencies() >> ['cola': ['contentTypeTemplate': 'application/vnd.cola.$version+json',
                                                             'version'            : 'v1',
                                                             'headers'            : ['header1': 'value1', 'header2': 'value2']]]
    }

    def 'should send a request to provided URL with appending host when calling service'() {
        given:
            String path = 'some/serviceUrl'
            URI expectedUri = new URI("${SOME_SERVICE_URL}/$path")
        and:
            aliasReturnsUrl(COLA_COLLABORATOR_NAME, SOME_SERVICE_URL)
        when:
            serviceRestClient.forService(COLA_COLLABORATOR_NAME).get().onUrl(path).ignoringResponse()
        then:
            1 * restOperations.exchange(expectedUri, GET, _ as HttpEntity, _ as Class)
    }

    def aliasReturnsUrl(String collaboratorName, String url) {
        serviceResolver.resolveAlias(new ServiceAlias(collaboratorName)) >> new ServicePath(collaboratorName)
        serviceResolver.fetchUri(new ServicePath(collaboratorName)) >> url.toURI()
    }

    def 'should send a request to provided URL with Content-Type set when calling service'() {
        given:
            String path = 'some/serviceUrl'
        and:
            aliasReturnsUrl(COLA_COLLABORATOR_NAME, SOME_SERVICE_URL)
        when:
            serviceRestClient.forService(COLA_COLLABORATOR_NAME).get().onUrl(path).ignoringResponse()
        then:
            1 * restOperations.exchange(_ as URI, GET, {
                it.getHeaders().getContentType().toString() == 'application/vnd.cola.v1+json'
            } as HttpEntity, _ as Class)
    }

    def 'should throw exception on creating Content-Type header when version property is required and is missing in dependency configuration'() {
        given:
            String path = 'some/serviceUrl'
        and:
            aliasReturnsUrl(COLA_COLLABORATOR_NAME, SOME_SERVICE_URL)
        when:
            serviceRestClient.forService(COLA_COLLABORATOR_NAME).get().onUrl(path).ignoringResponse()
        then:
            1 * configurationResolver.getDependencies() >> ['cola': ['contentTypeTemplate': 'application/vnd.cola.$version+json',
                                                                     'headers'            : ['header1': 'value1', 'header2': 'value2']]]
            thrown(MissingPropertyException)
    }

    def 'should send a request to provided URL with predefined headers set when calling service'() {
        given:
            String serviceUrl = SOME_SERVICE_URL
            String path = 'some/serviceUrl'
        and:
            aliasReturnsUrl(COLA_COLLABORATOR_NAME, serviceUrl)
        when:
            serviceRestClient.forService(COLA_COLLABORATOR_NAME).get().onUrl(path).ignoringResponse()
        then:
            1 * restOperations.exchange(_ as URI, GET, {
                it.getHeaders().get('header1') == ['value1'] as List &&
                        it.getHeaders().get('header2') == ['value2'] as List
            } as HttpEntity, _ as Class)
    }

    def 'should throw an exception when trying to access an unavailable service'() {
        given:
            serviceResolver.resolveAlias(new ServiceAlias(COLA_COLLABORATOR_NAME)) >> new ServicePath(COLA_COLLABORATOR_NAME)
            serviceResolver.fetchUri(new ServicePath(COLA_COLLABORATOR_NAME)) >> {
                throw new ServiceUnavailableException(COLA_COLLABORATOR_NAME)
            }
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

    def 'should retry once in case of failure'() {
        given:
            AsyncRetryExecutor executor = new AsyncRetryExecutor(pool)
        when:
            serviceRestClient
                    .forExternalService()
                    .retryUsing(executor.withMaxRetries(1).withNoDelay())
                    .get()
                    .onUrl(SOME_SERVICE_URL)
                    .ignoringResponse()
        then:
            2 * restOperations.exchange(_, GET, _, _ as Class) >>> [] >> {
                throw new RestClientException("Simulated")
            } >> null
    }

    def 'should retry once asynchronously in case of failure'() {
        given:
            AsyncRetryExecutor executor = new AsyncRetryExecutor(pool)
        when:
            ListenableFuture<String> future = serviceRestClient
                    .forExternalService()
                    .retryUsing(executor.withMaxRetries(1).withNoDelay())
                    .post()
                    .onUrl(SOME_SERVICE_URL)
                    .body('')
                    .andExecuteFor()
                    .aResponseEntity().ofTypeAsync(String)
            future.get()
        then:
            2 * restOperations.exchange(_, POST, _ as HttpEntity, _ as Class) >>> [] >> {
                throw new RestClientException("Simulated")
            } >> null
    }

    def 'should fail when RestOperations failed and no retry mechanism'() {
        given:
            AsyncRetryExecutor executor = new AsyncRetryExecutor(pool)
        when:
            serviceRestClient
                    .forExternalService()
                    .retryUsing(executor.withMaxRetries(0))
                    .delete()
                    .onUrl(SOME_SERVICE_URL)
                    .ignoringResponseAsync().get()
        then:
            1 * restOperations.exchange(_, DELETE, _ as HttpEntity, _ as Class) >> {
                throw new RestClientException("Simulated")
            }
            Exception e = thrown(Exception)
            e.message.contains("Simulated")
    }

    def 'should invoke HEAD async with retry in case first invocation fails'() {
        given:
            AsyncRetryExecutor executor = new AsyncRetryExecutor(pool)
        when:
            serviceRestClient
                    .forExternalService()
                    .retryUsing(executor.withMaxRetries(1).withNoDelay())
                    .head()
                    .onUrl(SOME_SERVICE_URL)
                    .httpHeadersAsync().get()
        then:
            2 * restOperations.exchange(_, HEAD, _ as HttpEntity, _ as Class) >>> [] >> {
                throw new RestClientException("Simulated")
            } >> null
    }

    def 'should fail when multiple retries of PUT failed'() {
        given:
            AsyncRetryExecutor executor = new AsyncRetryExecutor(pool)
        when:
            serviceRestClient
                    .forExternalService()
                    .retryUsing(executor.withMaxRetries(2).withNoDelay())
                    .put()
                    .onUrl(SOME_SERVICE_URL)
                    .body('')
                    .ignoringResponseAsync()
                    .get()
        then:
            3 * restOperations.exchange(_, PUT, _ as HttpEntity, _ as Class) >>> [] >> {
                throw new RestClientException("Simulated A")
            } >> {
                throw new RestClientException("Simulated B")
            } >> {
                throw new RestClientException("Simulated C")
            } >> null
            Exception e = thrown(Exception)
            e.message.contains("Simulated C")
    }

    def 'should wrap HTTP call inside Hystrix command'() {
        given:
            HystrixCommand.Setter circuitBreaker = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("Command"))
        when:
            serviceRestClient
                    .forExternalService()
                    .put()
                    .withCircuitBreaker(circuitBreaker)
                    .onUrl(SOME_SERVICE_URL)
                    .body('')
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(_, _, _ as HttpEntity, _ as Class) >> {
                assert runsInHystrixThread()
                return null
            }
    }

    def 'should not propagate Hystrix exceptions but unwrap them'() {
        given:
            HystrixCommand.Setter circuitBreaker = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("Command"))
        and:
            restOperations.exchange(_, GET, _ as HttpEntity, _ as Class) >> {
                throw new RestClientException("Simulated A")
            }
        when:
            serviceRestClient
                    .forExternalService()
                    .get()
                    .withCircuitBreaker(circuitBreaker)
                    .onUrl(SOME_SERVICE_URL)
                    .ignoringResponse()

        then:
            thrown(RestClientException)
    }

    def 'should use hystrix fallback with response entity when provided instead of throwing unwrapped exception'() {
        given:
            HystrixCommand.Setter circuitBreaker = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("Command"))
            String fallbackText = 'some text'
            Integer fallbackStatus = 201
            Closure<ResponseEntity<String>> fallbackClosure =  {
                ResponseEntity.status(fallbackStatus).body(fallbackText)
            }

        and:
            restOperations.exchange(_, GET, _ as HttpEntity, _ as Class) >> {
                throw new RestClientException("Simulated A")
            }
        when:
            ResponseEntity<String> response = serviceRestClient
                    .forExternalService()
                    .get()
                    .withCircuitBreaker(circuitBreaker, fallbackClosure)
                    .onUrl(SOME_SERVICE_URL)
                    .andExecuteFor()
                    .aResponseEntity().ofType(String)
        then:
            response.body == fallbackText
            response.statusCode.value() == fallbackStatus
    }

    def 'should use hystrix fallback with passed body when provided instead of throwing unwrapped exception'() {
        given:
            HystrixCommand.Setter circuitBreaker = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("Command"))
            String fallbackText = 'some text'
            Closure<String> fallbackClosure =  {
                fallbackText
            }
        and:
            restOperations.exchange(_, GET, _ as HttpEntity, _ as Class) >> {
                throw new RestClientException("Simulated A")
            }
        when:
            ResponseEntity<String> response = serviceRestClient
                    .forExternalService()
                    .get()
                    .withCircuitBreaker(circuitBreaker, fallbackClosure)
                    .onUrl(SOME_SERVICE_URL)
                    .andExecuteFor()
                    .aResponseEntity().ofType(String)

        then:
            response.body == fallbackText
    }

    def 'should not wrap HTTP call inside Hystrix command if not requested'() {
        when:
            serviceRestClient
                    .forExternalService()
                    .put()
                    .onUrl(SOME_SERVICE_URL)
                    .body('')
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(_, _, _ as HttpEntity, _ as Class) >> {
                assert !runsInHystrixThread()
                return null
            }
    }

    private boolean runsInHystrixThread() {
        return Thread.currentThread().name.startsWith("hystrix-")
    }

}
