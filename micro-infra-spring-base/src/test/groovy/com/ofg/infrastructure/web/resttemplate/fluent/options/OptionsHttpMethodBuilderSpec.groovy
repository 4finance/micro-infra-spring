package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.OPTIONS
import static org.springframework.http.HttpStatus.OK

class OptionsHttpMethodBuilderSpec extends HttpMethodSpec {

    public static final Class<String> RESPONSE_TYPE = String

    def "should use only url template without provided service url to retrieve an object"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
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
            1 * restOperations.exchange(templateUrl,
                    OPTIONS,
                    _ as HttpEntity,
                    RESPONSE_TYPE,
                    OBJECT_ID)
    }
    
    def "should use only url template without provided service url to retrieve allow header"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
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
            1 * restOperations.exchange(templateUrl,
                    OPTIONS,
                    _ as HttpEntity,
                    _ as Class,
                    OBJECT_ID) >> responseEntity
            expectedHttpMethods == actualHttpHeaders
    }
    
    def "should use only url template without provided service url to retrieve ResponseEntity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
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
            1 * restOperations.exchange(templateUrl,
                            OPTIONS,
                            _ as HttpEntity,
                            RESPONSE_TYPE,
                            OBJECT_ID) >> expectedResponseEntity
            expectedResponseEntity == actualResponseEntity
    }
    
    def "should use only url template from map without provided service url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
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
            1 * restOperations.exchange(templateUrl,
                    OPTIONS,
                    _ as HttpEntity,
                    RESPONSE_TYPE,
                    [objectId: OBJECT_ID]) 
    }

    def "should add service url to template when provided"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                .options()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(FULL_URL, OPTIONS, _ as HttpEntity, RESPONSE_TYPE, OBJECT_ID)
    }

    def "should treat url as path when sending request to a service with a path containing a slash"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:            
            httpMethodBuilder
                .options()
                    .onUrl(PATH_WITH_SLASH)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), OPTIONS, _ as HttpEntity, RESPONSE_TYPE)
    }
    
    def "should treat String url as path when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:            
            httpMethodBuilder
                .options()
                    .onUrl(PATH)
                    .andExecuteFor()
                    .aResponseEntity()
                    .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), OPTIONS, _ as HttpEntity, RESPONSE_TYPE)
    }

    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String url = 'http://some.url/api/objects'
        when:
            httpMethodBuilder
                    .options()
                    .onUrl(url)
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(url),
                    OPTIONS,
                    _ as HttpEntity,
                    Object)
    }

    def "should add parameters to query string when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .options()
                    .onUrl(PATH)
                    .withQueryParameters(['parameterOne': 'valueOne', 'parameterTwo': null])
                    .andExecuteFor()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL + "?parameterOne=valueOne&parameterTwo"),
                    OPTIONS,
                    new HttpEntity(null),
                    Object)
    }

    def "should add parameters to query string when sending request to a service via DSL"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .options()
                    .onUrl(PATH)
                    .withQueryParameters()
                        .parameter("size",123)
                        .parameter("sort",null)
                        .parameter("filter","")
                    .andExecuteFor()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL + "?filter&size=123&sort"),
                    OPTIONS,
                    _ as HttpEntity,
                    Object)
    }

}
