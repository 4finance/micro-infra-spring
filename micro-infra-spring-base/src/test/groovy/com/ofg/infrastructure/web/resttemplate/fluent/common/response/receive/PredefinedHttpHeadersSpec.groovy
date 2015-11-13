package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive
import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.ServicePath
import com.ofg.infrastructure.discovery.util.LoadBalancerType
import org.springframework.http.HttpHeaders
import spock.lang.Specification

class PredefinedHttpHeadersSpec extends Specification {

    def 'should set Content-Type header if template is defined'() {
        given:
            MicroserviceConfiguration.Dependency serviceConfig = new MicroserviceConfiguration.Dependency(null,
                    new ServicePath(''), false, LoadBalancerType.ROUND_ROBIN, 'application/vnd.some-service.$version+json', 'v1', [:])
            PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders(serviceConfig)
            HttpHeaders httpHeaders = new HttpHeaders()
        when:
            predefinedHeaders.copyTo(httpHeaders)
        then:
            httpHeaders.getContentType().toString() == 'application/vnd.some-service.v1+json'
    }

    def 'should not set Content-Type header if there is no template defined'() {
        given:
            PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders()
            HttpHeaders httpHeaders = new HttpHeaders()
        when:
            predefinedHeaders.copyTo(httpHeaders)
        then:
            httpHeaders.getContentType() == null
            httpHeaders.get('Content-Type') == null
    }

    def 'should copy existing predefined headers to HTTP headers'() {
        given:
            MicroserviceConfiguration.Dependency serviceConfig = new MicroserviceConfiguration.Dependency(null,
                    new ServicePath(''), false, LoadBalancerType.ROUND_ROBIN, '', '', ['header1':'value1', 'header2':'value2'])
            PredefinedHttpHeaders predefinedHeaders = new PredefinedHttpHeaders(serviceConfig)
            HttpHeaders httpHeaders = new HttpHeaders()
        when:
            predefinedHeaders.copyTo(httpHeaders)
        then:
            httpHeaders.get('header1') == ['value1'] as List
            httpHeaders.get('header2') == ['value2'] as List
    }

}
