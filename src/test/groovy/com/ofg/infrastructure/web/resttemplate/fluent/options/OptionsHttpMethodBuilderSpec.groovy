package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.OPTIONS
import static org.springframework.http.HttpStatus.OK

@TypeChecked
class OptionsHttpMethodBuilderSpec extends HttpMethodSpec {

    public static final String RESPONSE_BODY = '''{"sample":"response"}'''
    public static final Class<String> RESPONSE_TYPE = String

    def "should use only url template without provided service url to retrieve an object"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                            .options()
                                .onUrlFromTemplate(templateUrl)            
                                    .withVariables(OBJECT_ID)
                                .andExecuteFor()
                                    .anObject()
                                    .ofType(RESPONSE_TYPE)
        then:
            1 * restTemplate.exchange(templateUrl,
                    OPTIONS,
                    _ as HttpEntity,
                    RESPONSE_TYPE,
                    OBJECT_ID)
    }
    
    def "should use only url template without provided service url to retrieve allow header"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            Set<HttpMethod> expectedHttpMethods = [OPTIONS, DELETE] as Set<HttpMethod>
            HttpHeaders httpHeaders = new HttpHeaders(allow: expectedHttpMethods)
            ResponseEntity<Object> responseEntity = new ResponseEntity<>(httpHeaders, OK)
        when:
            Set<HttpMethod> actualHttpHeaders = httpMethodBuilder
                            .options()
                                .onUrlFromTemplate(templateUrl)            
                                    .withVariables(OBJECT_ID)
                                .andExecuteFor()
                                .allow()
        then:
            1 * restTemplate.exchange(templateUrl,
                    OPTIONS,
                    _ as HttpEntity,
                    _ as Class,
                    OBJECT_ID) >> responseEntity
            expectedHttpMethods == actualHttpHeaders
    }
    
    def "should use only url template without provided service url to retrieve ResponseEntity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(OK)
        when:
            ResponseEntity actualResponseEntity = httpMethodBuilder
                                                                    .options()
                                                                        .onUrlFromTemplate(templateUrl)
                                                                            .withVariables(OBJECT_ID)
                                                                        .andExecuteFor()
                                                                            .aResponseEntity()
                                                                            .ofType(RESPONSE_TYPE)
        then:
            1 * restTemplate.exchange(templateUrl,
                            OPTIONS,
                            _ as HttpEntity,
                            RESPONSE_TYPE,
                            OBJECT_ID) >> expectedResponseEntity
            expectedResponseEntity == actualResponseEntity
    }
    
    def "should use only url template from map without provided service url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                .options()
                    .onUrlFromTemplate(templateUrl)            
                        .withVariables([objectId: OBJECT_ID])
                    .andExecuteFor()
                        .aResponseEntity()
                        .ofType(RESPONSE_TYPE)
        then:
            1 * restTemplate.exchange(templateUrl,
                    OPTIONS,
                    _ as HttpEntity,
                    RESPONSE_TYPE,
                    [objectId: OBJECT_ID]) 
    }

    def "should add service url to template when provided"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
        when:
            httpMethodBuilder
                .options()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(RESPONSE_TYPE)
        then:
            1 * restTemplate.exchange(FULL_URL, OPTIONS, _ as HttpEntity, RESPONSE_TYPE, OBJECT_ID)
    }

    def "should ignore service url when passing full URL"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
            URI url = new URI('http://some.url/api/objects/42')
        when:            
            httpMethodBuilder
                .options()
                    .onUrl(url)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(RESPONSE_TYPE)
        then:
            1 * restTemplate.exchange(url, OPTIONS, _ as HttpEntity, RESPONSE_TYPE)
    }
    
    def "should ignore service url when passing full URL as String"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restTemplate)
            String url = 'http://some.url/api/objects/42'
        when:            
            httpMethodBuilder
                .options()
                    .onUrl(url)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(RESPONSE_TYPE)
        then:
            1 * restTemplate.exchange(new URI(url), OPTIONS, _ as HttpEntity, RESPONSE_TYPE)
    }

    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String url = 'http://some.url/api/objects'
        when:
            httpMethodBuilder
                    .options()
                    .onUrl(url)
                    .ignoringResponse()
        then:
            1 * restTemplate.exchange(new URI(url),
                    OPTIONS,
                    _ as HttpEntity,
                    Object)

    }

}
