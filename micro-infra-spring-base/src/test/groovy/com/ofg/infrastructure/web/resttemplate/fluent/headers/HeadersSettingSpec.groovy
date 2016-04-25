package com.ofg.infrastructure.web.resttemplate.fluent.headers

import com.ofg.infrastructure.discovery.MicroserviceConfiguration
import com.ofg.infrastructure.discovery.util.DependencyCreator
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.HttpMethodSpec
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import spock.lang.Unroll

import static com.ofg.infrastructure.web.resttemplate.fluent.HTTPAuthorizationUtils.encodeCredentials
import static org.springframework.http.HttpMethod.*
import static org.springframework.http.MediaType.APPLICATION_JSON

class HeadersSettingSpec extends HttpMethodSpec {

    public static final String TEMPLATE_URL = 'http://some.url/api/objects/{objectId}'
    private static final MicroserviceConfiguration.Dependency ADDITIONAL_HEADERS_CONFIG = DependencyCreator.fromMap('asd': ['headers': ['header1': 'value1', 'header2': 'value2']]).first()
    private static final MicroserviceConfiguration.Dependency CONTENT_TYPE_HEADER_CONFIG = DependencyCreator.fromMap('asd' : ['contentTypeTemplate': 'application/vnd.external-service.$version+json', 'version': 'v1']).first()
    private static final String EXPECTED_CONTENT_TYPE = 'application/vnd.external-service.v1+json'

    def "should fill out headers for get method"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(TEMPLATE_URL)            
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .header('key', 'value')
                        .headers([anotherKey : 'value'])
                        .accept(APPLICATION_JSON)
                        .cacheControl('no-cache')
                        .contentType('application/vnd.mymoid-adapter.v1+json')
                        .expires(1000)
                        .lastModified(2000)
                        .location(new URI('localhost'))
                    .andExecuteFor()                    
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    GET,
                    { HttpEntity httpEntity -> httpEntity.headers.keySet().every isPresentInSetHeaders() } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should fill out headers for post method"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                .post()
                    .onUrlFromTemplate(TEMPLATE_URL)            
                        .withVariables(OBJECT_ID)
                    .body('')
                    .withHeaders()
                        .header('key', 'value')
                        .headers([anotherKey : 'value'])
                        .accept(APPLICATION_JSON)
                        .cacheControl('no-cache')
                        .contentType('application/vnd.mymoid-adapter.v1+json')
                        .expires(1000)
                        .lastModified(2000)
                        .location(new URI('localhost'))
                    .andExecuteFor()                    
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    POST,
                    { HttpEntity httpEntity -> httpEntity.headers.keySet().every isPresentInSetHeaders() } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should fill out headers for head method"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .head()
                        .onUrlFromTemplate(TEMPLATE_URL)
                            .withVariables(OBJECT_ID)
                        .withHeaders()
                            .header('key', 'value')
                            .headers([anotherKey : 'value'])
                            .accept(APPLICATION_JSON)
                            .cacheControl('no-cache')
                            .contentType('application/vnd.mymoid-adapter.v1+json')
                            .expires(1000)
                            .lastModified(2000)
                            .location(new URI('localhost'))
                        .andExecuteFor()
                        .aResponseEntity()
            then:
        1 * restOperations.exchange(TEMPLATE_URL,
                HEAD,
                { HttpEntity httpEntity -> httpEntity.headers.keySet().every isPresentInSetHeaders() } as HttpEntity,
                _ as Class,
                _ as Long)
    }

    def "should fill out headers for options method"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .options()
                        .onUrlFromTemplate(TEMPLATE_URL)
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .header('key', 'value')
                        .headers([anotherKey : 'value'])
                        .accept(APPLICATION_JSON)
                        .cacheControl('no-cache')
                        .contentType('application/vnd.mymoid-adapter.v1+json')
                        .expires(1000)
                        .lastModified(2000)
                        .location(new URI('localhost'))
                    .andExecuteFor()
                        .aResponseEntity()
                        .ofType(String)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    OPTIONS,
                    { HttpEntity httpEntity -> httpEntity.headers.keySet().every isPresentInSetHeaders() } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should fill out headers for delete method"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .delete()
                        .onUrlFromTemplate(TEMPLATE_URL)
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .header('key', 'value')
                        .headers([anotherKey : 'value'])
                        .accept(APPLICATION_JSON)
                        .cacheControl('no-cache')
                        .contentType('application/vnd.mymoid-adapter.v1+json')
                        .expires(1000)
                        .lastModified(2000)
                        .location(new URI('localhost'))
                    .andExecuteFor()
                        .aResponseEntity()
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    DELETE,
                    { HttpEntity httpEntity -> httpEntity.headers.keySet().every isPresentInSetHeaders() } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should send a request from passed HttpEntity"() {
        given:
            String body = '''{"some":"body"}'''
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .post()
                        .onUrlFromTemplate(TEMPLATE_URL)
                        .withVariables(OBJECT_ID)     
                    .httpEntity(new HttpEntity(body, createHeaders()))                    
                    .ignoringResponse()
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    POST,
                    { HttpEntity httpEntity -> 
                        httpEntity.headers.keySet().every(isPresentInSetHeaders()) && httpEntity.body == body } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should add custom headers to request when predefined headers provided"() {
        given:
            PredefinedHttpHeaders predefinedHttpHeaders = new PredefinedHttpHeaders(ADDITIONAL_HEADERS_CONFIG)
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, predefinedHttpHeaders, tracingInfo)
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(URL_TEMPLATE)
                        .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(FULL_URL, GET, {
                HttpEntity httpEntity -> hasExpectedAdditionalHeaders(httpEntity)
            } as HttpEntity, BigDecimal, OBJECT_ID)
    }

    def "should set Content-Type header when template with version is provided"() {
        given:
            PredefinedHttpHeaders predefinedHttpHeaders = new PredefinedHttpHeaders(CONTENT_TYPE_HEADER_CONFIG)
            httpMethodBuilder = new HttpMethodBuilder(SERVICE_URL, restOperations, predefinedHttpHeaders, tracingInfo)
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(URL_TEMPLATE)
                       .withVariables(OBJECT_ID)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(FULL_URL, GET, {
                HttpEntity httpEntity -> hasExpectedContentTypeHeader(httpEntity)
            } as HttpEntity, BigDecimal, OBJECT_ID)
    }

    def "should fill out content type from media type"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                .get()
                    .onUrlFromTemplate(TEMPLATE_URL)            
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .contentType(new MediaType('application', 'vnd.mymoid-adapter.v1+json'))
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    GET,
                    { HttpEntity httpEntity -> httpEntity.headers.keySet().every { it in ['Content-Type'] } } as  HttpEntity,
                    _ as Class,
                    _ as Long)
            
    }

    def "should fill out headers from HttpHeaders"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
            HttpHeaders headers = createHeaders()
        when:
            httpMethodBuilder
                    .get()
                        .onUrlFromTemplate(TEMPLATE_URL)
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .headers(headers)
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    GET,
                    { HttpEntity httpEntity -> httpEntity.headers.keySet().every isPresentInSetHeaders() } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should fill out JSON content type header"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .get()
                        .onUrlFromTemplate(TEMPLATE_URL)
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .contentTypeJson()
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    GET,
                    { HttpEntity httpEntity ->
                        httpEntity.headers['Content-Type'] == [MediaType.APPLICATION_JSON_VALUE]
                    } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should fill out XML content type header"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .get()
                        .onUrlFromTemplate(TEMPLATE_URL)
                        .withVariables(OBJECT_ID)
                    .withHeaders()
                        .contentTypeXml()
                    .andExecuteFor()
                        .anObject()
                        .ofType(BigDecimal)
        then:
            1 * restOperations.exchange(TEMPLATE_URL,
                    GET,
                    { HttpEntity httpEntity -> httpEntity.headers['Content-Type'] == [MediaType.APPLICATION_XML_VALUE] } as HttpEntity,
                    _ as Class,
                    _ as Long)
    }

    def "should have 'Authorization' header with value: '#authorizationValue'"() {
        given:
        httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
        httpMethodBuilder
                .get()
                .onUrlFromTemplate(TEMPLATE_URL)
                .withVariables(OBJECT_ID)
                .withHeaders()
                .basicAuthentication(username, password)
                .andExecuteFor()
                .anObject()
                .ofType(BigDecimal)
        then:
        1 * restOperations.exchange(TEMPLATE_URL,
                GET,
                { HttpEntity httpEntity -> httpEntity.headers['Authorization'] == [authorizationValue] } as HttpEntity,
                _ as Class,
                _ as Long)
        where:
            username = "AuthUsername"
            password = "AuthPassword"
            authorizationValue = ("Basic " + encodeCredentials(username, password)) as String
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders(
                accept: [APPLICATION_JSON],
                cacheControl: 'no-cache',
                contentType: new MediaType('application', 'vnd.mymoid-adapter.v1+json'),
                expires: 1000,
                lastModified: 2000,
                location: new URI('localhost')
        )
        headers.set('key', 'value')
        headers.set('anotherKey', 'value')
        return headers
    }

    public final Closure<Boolean> isPresentInSetHeaders() {
        return { it in ['key', 'anotherKey', 'Accept', 'Cache-Control', 'Content-Type', 'Expires', 'Last-Modified', 'Location'] }
    }

    private boolean hasExpectedContentTypeHeader(HttpEntity entity) {
        return entity.getHeaders().getContentType().toString() == EXPECTED_CONTENT_TYPE
    }

    private boolean hasExpectedAdditionalHeaders(HttpEntity entity) {
        return entity.getHeaders().get('header1') == ['value1'] as List &&
                entity.getHeaders().get('header2') == ['value2'] as List
    }

}
