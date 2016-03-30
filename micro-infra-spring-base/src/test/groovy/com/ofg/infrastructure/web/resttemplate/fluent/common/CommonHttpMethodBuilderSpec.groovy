package com.ofg.infrastructure.web.resttemplate.fluent.common

import com.ofg.infrastructure.discovery.ServiceUnavailableException
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import static org.springframework.http.HttpMethod.*

class CommonHttpMethodBuilderSpec extends HttpMethodSpec {
    
    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String url = 'http://some.url/api/objects'
            HttpEntity expectedHttpEntity = new HttpEntity('')
        when:
            httpMethodBuilder
                            ."${method.toString().toLowerCase()}"()
                                .onUrl(url)          
                                .httpEntity(expectedHttpEntity)
                                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(url),
                    method as HttpMethod,
                    expectedHttpEntity,
                    Object)
        where:
            method << [GET, HEAD, OPTIONS, POST, PUT, DELETE]
            
    }

    def "should fail at last possible step to send a request when service is not found"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder({
                throw new ServiceUnavailableException('someService')
            }, restOperations, new PredefinedHttpHeaders(), tracingInfo)
            String url = 'http://some.url/api/objects'
            HttpEntity expectedHttpEntity = new HttpEntity('')
        and:
            def methodBuilder = httpMethodBuilder
                            ."${method.toString().toLowerCase()}"()
                            .onUrl(url)
                            .httpEntity(expectedHttpEntity)
        when:
                  methodBuilder.ignoringResponse()
        then:
            thrown(ServiceUnavailableException)
        where:
            method << [GET, HEAD, OPTIONS, POST, PUT, DELETE]

    }

}
