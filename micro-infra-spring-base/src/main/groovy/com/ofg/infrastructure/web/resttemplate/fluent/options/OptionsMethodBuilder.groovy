package com.ofg.infrastructure.web.resttemplate.fluent.options
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ObjectReceiving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.ResponseEntityReceiving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#HEAD method} fluent API
 */
@TypeChecked
class OptionsMethodBuilder implements
        OptionsMethod, UrlParameterizableOptionsMethod,
        ResponseReceivingOptionsMethod, HeadersHaving, AllowHeaderReceiving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    @Delegate private final AllowContainingWithHeaders withHeaders
    @Delegate private final OptionsAllowHeaderExecutor allowHeaderExecutor

    OptionsMethodBuilder(String host, RestOperations restOperations) {
        this.restOperations = restOperations
        params.host = host
        withHeaders = new AllowContainingWithHeaders(this, params)
        allowHeaderExecutor = new OptionsAllowHeaderExecutor(params, restOperations)
    }

    OptionsMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations)
    }

    @Override
    ResponseReceivingOptionsMethod onUrl(URI url) {
        params.url = url
        return this
    }
    
    @Override
    ResponseReceivingOptionsMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingOptionsMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    @Override
    UrlParameterizableOptionsMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingOptionsMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    Set<HttpMethod> allow() {
        return allowHeaderExecutor.allow()
    }

    @Override
    ObjectReceiving anObject() {
        return new ObjectReceiving() {
            @Override
            public <T> T ofType(Class<T> responseType) {
                return new OptionsExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()?.body
            }
        }
    }

    @Override
    ResponseEntityReceiving aResponseEntity() {
        return new ResponseEntityReceiving() {
            @Override
            public <T> ResponseEntity<T> ofType(Class<T> responseType) {
                return new OptionsExecuteForResponseTypeRelated<T>(params, restOperations, responseType).exchange()
            }
        }
    }

    @Override
    void ignoringResponse() {
        aResponseEntity().ofType(Object)
    }

}
