package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpStatus.OK

class PostHttpMethodBuilderSpec extends HttpMethodSpec {

    public static final String REQUEST_BODY = '''{"sample":"request"}'''
    public static final String RESPONSE_BODY = '''{"sample":"response"}'''
    public static final Class<String> RESPONSE_TYPE = String

    def "should query for location when sending a post request on given address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            URI expectedLocation = new URI('http://localhost')
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrl(expectedLocation)
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(expectedLocation, 
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }
    
    def "should query for location when sending a post request on given address as String"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String expectedLocationAsString = 'http://localhost'
            URI expectedLocation = new URI(expectedLocationAsString)
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrl(expectedLocation)
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(expectedLocation, 
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }
    
    def "should query for location when sending a post request on given template address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            URI expectedLocation = new URI('localhost')
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrlFromTemplate(templateUrl)
                                                        .withVariables(OBJECT_ID)
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(templateUrl, 
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE , 
                                      OBJECT_ID) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }

    def "should query for location when sending a post request on service template address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
            URI expectedLocation = new URI('localhost')
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrlFromTemplate(URL_TEMPLATE)
                                                        .withVariables(OBJECT_ID)
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(FULL_URL, 
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE, 
                                      OBJECT_ID) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }

    def "should query for location when sending a post request on service template address using map for url vars"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
            URI expectedLocation = new URI('localhost')
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrlFromTemplate(URL_TEMPLATE)
                                                        .withVariables([objectId: OBJECT_ID])
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(FULL_URL, 
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE,
                                      [objectId: OBJECT_ID]) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }

    def "should query for location when sending a post request on service template address with url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
            URI expectedLocation = new URI(FULL_SERVICE_URL)
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrl(PATH)
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL),
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }

    def "should query for location when sending a post request on service template address with String url"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
            URI expectedLocation = new URI('localhost')
        when:
            URI actualLocation = httpMethodBuilder
                                                .post()
                                                    .onUrlFromTemplate(URL_TEMPLATE)
                                                        .withVariables([objectId: OBJECT_ID])
                                                    .body(REQUEST_BODY)
                                                    .forLocation()
        then:
            1 * restOperations.exchange(FULL_URL, 
                                      POST, 
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity, 
                                      RESPONSE_TYPE,
                                      [objectId: OBJECT_ID]) >> responseEntityWith(expectedLocation)
            actualLocation == expectedLocation
    }

    def "should query for object when sending a post request on given template address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
        when:
            String actualResponseBody = httpMethodBuilder
                                                        .post()
                                                            .onUrlFromTemplate(templateUrl)
                                                                .withVariables(OBJECT_ID)
                                                            .body(REQUEST_BODY)
                                                            .andExecuteFor()
                                                                .anObject()
                                                                .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(templateUrl,
                                      POST,
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity,
                                      RESPONSE_TYPE,
                                      OBJECT_ID) >> responseEntityWith(RESPONSE_BODY)
            actualResponseBody == RESPONSE_BODY
    }

    def "should query for object when sending a post request on service template address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            String actualResponseBody = httpMethodBuilder
                                                        .post()
                                                            .onUrlFromTemplate(URL_TEMPLATE)
                                                                .withVariables(OBJECT_ID)
                                                            .body(REQUEST_BODY)
                                                            .andExecuteFor()
                                                                .anObject()
                                                                .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(FULL_URL,
                                      POST,
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity,
                                      RESPONSE_TYPE,
                                      OBJECT_ID) >> responseEntityWith(RESPONSE_BODY)
            actualResponseBody == RESPONSE_BODY
    }

    def "should query for entity when sending a post request on given template address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String templateUrl = 'http://some.url/api/objects/{objectId}'
            ResponseEntity<String> expectedResponseEntity = responseEntityWith(RESPONSE_BODY)
        when:
            ResponseEntity<String> actualResponseEntity = httpMethodBuilder
                                                                        .post()
                                                                            .onUrlFromTemplate(templateUrl)
                                                                                .withVariables(OBJECT_ID)
                                                                            .body(REQUEST_BODY)
                                                                            .andExecuteFor()
                                                                                .aResponseEntity()
                                                                                .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(templateUrl,
                                      POST,
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity,
                                      RESPONSE_TYPE,
                                      OBJECT_ID) >> expectedResponseEntity
            actualResponseEntity == expectedResponseEntity
    }

    def "should query for entity when sending a post request on service template address"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
            ResponseEntity<String> expectedResponseEntity = responseEntityWith(RESPONSE_BODY)
        when:
            ResponseEntity<String> actualResponseEntity = httpMethodBuilder
                    .post()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .body(REQUEST_BODY)
                    .andExecuteFor()
                        .aResponseEntity()
                        .ofType(RESPONSE_TYPE)
        then:
            1 * restOperations.exchange(FULL_URL,
                                      POST,
                                      { HttpEntity httpEntity -> httpEntity.body == REQUEST_BODY } as HttpEntity,
                                      RESPONSE_TYPE,
                                      OBJECT_ID) >> expectedResponseEntity
            actualResponseEntity == expectedResponseEntity
    }

    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            String url = 'http://some.url/api/objects'
        when:
            httpMethodBuilder
                    .post()
                    .onUrl(url)
                    .withoutBody()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(url),
                    POST,
                    _ as HttpEntity,
                    Object)

    }

    def "should treat url as path when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .post()
                    .onUrl(PATH_WITH_SLASH)
                    .withoutBody()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), POST, _ as HttpEntity, _ as Class)
    }

    def "should treat String url as path when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .post()
                    .onUrl(PATH)
                    .withoutBody()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL), POST, _ as HttpEntity, _ as Class)
    }

    def "should add parameters to query string when sending request to a service"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .post()
                    .onUrl(PATH)
                    .withQueryParameters(['parameterOne': 'valueOne', 'parameterTwo': null])
                    .andExecuteFor()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL + "?parameterOne=valueOne&parameterTwo"),
                    POST,
                    new HttpEntity(null),
                    Object)
    }

    def "should add parameters to query string when sending request to a service via DSL"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, NO_PREDEFINED_HEADERS, tracingInfo)
        when:
            httpMethodBuilder
                    .post()
                    .onUrl(PATH)
                    .withQueryParameters()
                        .parameter("size",123)
                        .parameter("sort",null)
                        .parameter("filter","")
                    .andExecuteFor()
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(new URI(FULL_SERVICE_URL + "?filter&size=123&sort"),
                    POST,
                    _ as HttpEntity,
                    Object)
    }

    private ResponseEntity responseEntityWith(URI expectedLocation) {
        new ResponseEntity<>(new HttpHeaders(location: expectedLocation), OK)
    }
    
    private ResponseEntity responseEntityWith(Object body) {
        new ResponseEntity<>(body, OK)
    }

}
