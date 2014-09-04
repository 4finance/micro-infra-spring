package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.GET

class GetHttpMethodBuilderSpec extends HttpMethodSpec {
    
    def "should use only url template without provided service url to retrieve object"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations)
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
            1 * restOperations.exchange(templateUrl,
                    GET,
                    _ as HttpEntity,
                    BigDecimal,
                    OBJECT_ID)
    }
    
    def "should use only url template without provided service url to retrieve ResponseEntity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations)
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
            1 * restOperations.exchange(templateUrl,
                            GET,
                            _ as HttpEntity,
                            BigDecimal,
                            OBJECT_ID) >> expectedResponseEntity
            expectedResponseEntity == actualResponseEntity
    }
    
    def "should use only url template from map without provided service url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations)
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
            1 * restOperations.exchange(templateUrl,
                    GET,
                    _ as HttpEntity,
                    BigDecimal,
                    [objectId: OBJECT_ID]) 
    }

    def "should add service url to template when provided"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations)
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(FULL_URL, GET, _ as HttpEntity, BigDecimal, OBJECT_ID)
    }

    def "should treat url as path when sending request to a service to a path conaining a slash"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations)
        when:            
            httpMethodBuilder
                .get()
                    .onUrl(PATH_WITH_SLASH)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), GET, _ as HttpEntity, BigDecimal)
    }

    def "should treat String url as path when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations)
        when:            
            httpMethodBuilder
                .get()
                    .onUrl(PATH)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), GET, _ as HttpEntity, BigDecimal)
    }

    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations)
            String url = 'http://some.url/api/objects'
        when:
            httpMethodBuilder
                    .get()
                    .onUrl(url)
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(url),
                    GET,
                    _ as HttpEntity,
                    Object)

    }

}
