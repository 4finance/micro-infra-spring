package com.ofg.infrastructure.web.resttemplate

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.infrastructure.discovery.ServiceDiscoveryConfiguration
import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestOperations

import javax.annotation.Resource
import java.util.concurrent.Callable

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(classes = [ServiceRestClientConfiguration, ServiceDiscoveryConfiguration, Config], loader = SpringApplicationContextLoader)
@TestPropertySource(properties = 'rest.client.metrics.enabled=true')
class RestOperationsMetricsAspectIntegrationSpec extends MvcWiremockIntegrationSpec {

    @Resource
    RestOperations restTemplate

    @Autowired
    MetricRegistry metricRegistry

    URI wireMockUri

    def setup() {
        wireMockUri = new URI(wireMockUrl)
        stubInteraction(any(urlMatching('/.*')), aResponse().withStatus(200))
    }

    def 'should elide subsequent path parts in metric name'() {
        expect:
            verifyMetric('.foo')     { restTemplate.delete("$wireMockUrl/foo") }
            verifyMetric('.foo._')   { restTemplate.delete("$wireMockUrl/foo/bar") }
            verifyMetric('.foo._._') { restTemplate.delete("$wireMockUrl/foo/bar/baz") }
            verifyMetric('.foo._._') { restTemplate.delete("$wireMockUrl/foo/bar/baz") }
    }

    def 'should include template parameters in metric name when they`re the first part of the path'() {
        expect:
            verifyMetric('.id')  { restTemplate.delete("$wireMockUrl/{id}", '42') }
            verifyMetric('.key') { restTemplate.delete("$wireMockUrl/{key}", [key: 'value']) }
            verifyMetric         { restTemplate.delete(wireMockUri) }
    }

    def 'should gather metrics for all RestOperations methods calls'() {
        given:
            def responseType = Void
            def parameterizedResponseType = new ParameterizedTypeReference<String>() {}
            def httpMethod = HttpMethod.GET
            def httpEntity = new HttpEntity<String>("Knock knock!")
            def requestEntity = new RequestEntity<Void>(httpMethod, wireMockUri)
            def requestCallback = {} as RequestCallback
            def requestExtractor = {} as ResponseExtractor
            def request = 'request'

        expect:
            verifyMetric { restTemplate.delete(wireMockUrl) }
            verifyMetric { restTemplate.delete(wireMockUrl, [:]) }
            verifyMetric { restTemplate.delete(wireMockUri) }

            verifyMetric { restTemplate.exchange(wireMockUrl, httpMethod, httpEntity, responseType) }
            verifyMetric { restTemplate.exchange(wireMockUrl, httpMethod, httpEntity, responseType, [:]) }
            verifyMetric { restTemplate.exchange(wireMockUri, httpMethod, httpEntity, responseType) }
            verifyMetric { restTemplate.exchange(wireMockUrl, httpMethod, httpEntity, parameterizedResponseType) }
            verifyMetric { restTemplate.exchange(wireMockUrl, httpMethod, httpEntity, parameterizedResponseType, [:]) }
            verifyMetric { restTemplate.exchange(wireMockUri, httpMethod, httpEntity, parameterizedResponseType) }
            verifyMetric { restTemplate.exchange(requestEntity, responseType) }
            verifyMetric { restTemplate.exchange(wireMockUrl, httpMethod, httpEntity, parameterizedResponseType, [:]) }

            verifyMetric { restTemplate.execute(wireMockUrl, httpMethod, requestCallback, requestExtractor) }
            verifyMetric { restTemplate.execute(wireMockUrl, httpMethod, requestCallback, requestExtractor, [:]) }
            verifyMetric { restTemplate.execute(wireMockUri, httpMethod, requestCallback, requestExtractor) }

            verifyMetric { restTemplate.getForEntity(wireMockUrl, responseType) }
            verifyMetric { restTemplate.getForEntity(wireMockUrl, responseType, [:]) }
            verifyMetric { restTemplate.getForEntity(wireMockUri, responseType) }

            verifyMetric { restTemplate.getForObject(wireMockUrl, responseType) }
            verifyMetric { restTemplate.getForObject(wireMockUrl, responseType, [:]) }
            verifyMetric { restTemplate.getForObject(wireMockUri, responseType) }

            verifyMetric { restTemplate.headForHeaders(wireMockUrl) }
            verifyMetric { restTemplate.headForHeaders(wireMockUrl, [:]) }
            verifyMetric { restTemplate.headForHeaders(wireMockUri) }

            verifyMetric { restTemplate.optionsForAllow(wireMockUrl) }
            verifyMetric { restTemplate.optionsForAllow(wireMockUrl, [:]) }
            verifyMetric { restTemplate.optionsForAllow(wireMockUri) }

            verifyMetric { restTemplate.postForEntity(wireMockUrl, request, responseType) }
            verifyMetric { restTemplate.postForEntity(wireMockUrl, request, responseType, [:]) }
            verifyMetric { restTemplate.postForEntity(wireMockUri, request, responseType) }

            verifyMetric { restTemplate.postForObject(wireMockUrl, request, responseType) }
            verifyMetric { restTemplate.postForObject(wireMockUrl, request, responseType, [:]) }
            verifyMetric { restTemplate.postForObject(wireMockUri, request, responseType) }

            verifyMetric { restTemplate.postForLocation(wireMockUrl, request) }
            verifyMetric { restTemplate.postForLocation(wireMockUrl, request, [:]) }
            verifyMetric { restTemplate.postForLocation(wireMockUri, request) }

            verifyMetric { restTemplate.put(wireMockUrl, request) }
            verifyMetric { restTemplate.put(wireMockUrl, request, [:]) }
            verifyMetric { restTemplate.put(wireMockUri, request) }
    }

    void verifyMetric(String expectedMetricNameSuffix = '', Closure closure) {
        closure()
        def expectedMetricNamePrefix = "RestOperations.localhost.$wireMockPort"
        def expectedMetricName = expectedMetricNamePrefix + expectedMetricNameSuffix
        assert metricRegistry.lastTimerMetricName() == expectedMetricName
        metricRegistry.resetLastTimerMetricName()
    }

    @EnableAspectJAutoProxy
    @EnableAutoConfiguration
    static class Config {

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer()
        }

        @Bean
        MetricRegistry metricRegistry() {
            def lastMetricName = 'UNSET'
            return [
                    timer: { String metricName ->
                        lastMetricName = metricName
                        [time: { Callable c -> c.call() }] as Timer
                    },
                    lastTimerMetricName: { lastMetricName },
                    resetLastTimerMetricName: { lastMetricName = 'UNSET' }
            ] as MetricRegistry
        }

        @Bean
        RestOperations arbitraryRestOperations() { new RestTemplate() }

    }

}
