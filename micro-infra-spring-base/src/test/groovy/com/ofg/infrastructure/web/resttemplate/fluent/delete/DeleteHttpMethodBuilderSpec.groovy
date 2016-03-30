package com.ofg.infrastructure.web.resttemplate.fluent.delete

import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpStatus.OK

class DeleteHttpMethodBuilderSpec extends HttpMethodSpec {

    def "should use only url template without provided service url to retrieve response entity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                    .delete()
                    .onUrlFromTemplate(templateUrl)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(templateUrl,
                    DELETE,
                    _ as HttpEntity,
                    Object,
                    OBJECT_ID)
    }

    def "should use only url template without provided service url to retrieve ResponseEntity"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            ResponseEntity<Object> expectedResponseEntity = new ResponseEntity<>(OK)
        when:
            ResponseEntity actualResponseEntity = httpMethodBuilder
                    .delete()
                    .onUrlFromTemplate(templateUrl)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(templateUrl,
                    DELETE,
                    _ as HttpEntity,
                    Object,
                    OBJECT_ID) >> expectedResponseEntity
            expectedResponseEntity == actualResponseEntity
    }

    def "should use only url template from map without provided service url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            httpMethodBuilder
                    .delete()
                    .onUrlFromTemplate(templateUrl)
                        .withVariables([objectId: OBJECT_ID])
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(templateUrl,
                    DELETE,
                    _ as HttpEntity,
                    Object,
                    [objectId: OBJECT_ID])
    }

    def "should add service url to template when provided"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(FULL_URL, DELETE, _ as HttpEntity, Object, OBJECT_ID)
    }

    def "should treat url as path when sending request to a service to a path containing a slash"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
            URI url = new URI(PATH_WITH_SLASH)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrl(url)
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), DELETE, _ as HttpEntity, Object)
    }

    def "should treat String url as path when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrl(PATH)
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), DELETE, _ as HttpEntity, Object)
    }

    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String url = 'http://some.url/api/objects'
        when:
            httpMethodBuilder
                    .delete()
                    .onUrl(url)
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(url),
                    DELETE,
                    _ as HttpEntity,
                    Object)
    }

    def "should add parameters to query string when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrl(PATH)
                    .withQueryParameters(['parameterOne': 'valueOne', 'parameterTwo': null])
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL + "?parameterOne=valueOne&parameterTwo"),
                    DELETE,
                    _ as HttpEntity,
                    Object)
    }

    def "should add parameters to query string when sending request to a service via DSL"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrl(PATH)
                    .withQueryParameters()
                        .parameter("size",123)
                        .parameter("sort",null)
                        .parameter("filter","")
                    .andExecuteFor()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL + "?filter&size=123&sort"),
                    DELETE,
                    _ as HttpEntity,
                    Object)
    }
}
