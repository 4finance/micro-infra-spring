package com.ofg.infrastructure.web.resttemplate.fluent.head
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.HEAD
import static org.springframework.http.HttpStatus.OK

@TypeChecked
class HeadHttpMethodBuilderSpec extends HttpMethodSpec {
    
    def "should use only url template without provided service url to retrieve response entity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                            .head()
                                .onUrlFromTemplate(templateUrl)            
                                    .withVariables(OBJECT_ID)
                                .andExecuteFor()
                                    .aResponseEntity()
        then:
            1 * restTemplate.exchange(templateUrl,
                    HEAD,
                    _ as HttpEntity,
                    Object,
                    OBJECT_ID)
    }
    
    def "should use only url template without provided service url to retrieve http response headers"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            HttpHeaders expectedHttpHeaders = new HttpHeaders()
            ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(expectedHttpHeaders, OK)
        when:
            HttpHeaders actualHttpHeaders = httpMethodBuilder
                            .head()
                                .onUrlFromTemplate(templateUrl)            
                                    .withVariables(OBJECT_ID)
                                .andExecuteFor()
                                    .httpHeaders()
        then:
            1 * restTemplate.exchange(templateUrl,
                    HEAD,
                    _ as HttpEntity,
                    Object,
                    OBJECT_ID) >> expectedResponseEntity
            expectedHttpHeaders == actualHttpHeaders
    }
    
    def "should use only url template without provided service url to retrieve ResponseEntity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(OK)
        when:
            ResponseEntity actualResponseEntity = httpMethodBuilder
                                                                    .head()
                                                                        .onUrlFromTemplate(templateUrl)
                                                                            .withVariables(OBJECT_ID)
                                                                        .andExecuteFor()
                                                                            .aResponseEntity()
        then:
            1 * restTemplate.exchange(templateUrl,
                            HEAD,
                            _ as HttpEntity,
                            Object,
                            OBJECT_ID) >> expectedResponseEntity
            expectedResponseEntity == actualResponseEntity
    }
    
    def "should use only url template from map without provided service url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                .head()
                    .onUrlFromTemplate(templateUrl)            
                        .withVariables([objectId: OBJECT_ID])
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restTemplate.exchange(templateUrl,
                    HEAD,
                    _ as HttpEntity,
                    Object,
                    [objectId: OBJECT_ID]) 
    }

    def "should add service url to template when provided"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
        when:
            httpMethodBuilder
                .head()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                    .aResponseEntity()
        then:
            1 * restTemplate.exchange(FULL_URL, HEAD, _ as HttpEntity, Object, OBJECT_ID)
    }

    def "should ignore service url when passing full URL"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
            URI url = new URI('http://some.url/api/objects/42')
        when:            
            httpMethodBuilder
                .head()
                    .onUrl(url)
                    .andExecuteFor()
                    .aResponseEntity()
        then:
            1 * restTemplate.exchange(url, HEAD, _ as HttpEntity, Object)
    }

    def "should ignore service url when passing full URL as String"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
            String url = 'http://some.url/api/objects/42'
        when:            
            httpMethodBuilder
                .head()
                    .onUrl(url)
                    .andExecuteFor()
                    .aResponseEntity()
        then:
            1 * restTemplate.exchange(new URI(url), HEAD, _ as HttpEntity, Object)
    }

}
