package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.GET

@TypeChecked
class GetHttpMethodBuilderSpec extends HttpMethodSpec {
    
    def "should use only url template without provided service url to retrieve object"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                            .get()
                                .onUrlFromTemplate(templateUrl)            
                                    .withVariables(OBJECT_ID)
                                .andExecuteFor()
                                    .anObject()
                                    .ofType(BigDecimal)
        then:
            1 * restTemplate.exchange(templateUrl,
                    GET,
                    _ as HttpEntity,
                    BigDecimal,
                    OBJECT_ID)
    }
    
    def "should use only url template without provided service url to retrieve ResponseEntity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            ResponseEntity<BigDecimal> expectedResponseEntity = new ResponseEntity<>(HttpStatus.OK)
        when:
            ResponseEntity<BigDecimal> actualResponseEntity = httpMethodBuilder
                                                                            .get()
                                                                                .onUrlFromTemplate(templateUrl)            
                                                                                    .withVariables(OBJECT_ID)
                                                                                .andExecuteFor()
                                                                                    .aResponseEntity()
                                                                                    .ofType(BigDecimal)
        then:
            1 * restTemplate.exchange(templateUrl,
                            GET,
                            _ as HttpEntity,
                            BigDecimal,
                            OBJECT_ID) >> expectedResponseEntity
            expectedResponseEntity == actualResponseEntity
    }
    
    def "should use only url template from map without provided service url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(templateUrl)            
                        .withVariables([objectId: OBJECT_ID])
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restTemplate.exchange(templateUrl,
                    GET,
                    _ as HttpEntity,
                    BigDecimal,
                    [objectId: OBJECT_ID]) 
    }

    def "should add service url to template when provided"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restTemplate.exchange(FULL_URL, GET, _ as HttpEntity, BigDecimal, OBJECT_ID)
    }

    def "should ignore service url when passing full URL"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
            URI url = new URI('http://some.url/api/objects/42')
        when:            
            httpMethodBuilder
                .get()
                    .onUrl(url)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restTemplate.exchange(url, GET, _ as HttpEntity, BigDecimal)
    }

}
