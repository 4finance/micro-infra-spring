package com.ofg.infrastructure.web.resttemplate.fluent.delete

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodylessWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

/**
 * Implementation of the {@link org.springframework.http.HttpMethod#DELETE method} fluent API
 */
@TypeChecked
class DeleteMethodBuilder implements DeleteMethod, UrlParameterizableDeleteMethod, ResponseReceivingDeleteMethod, HeadersHaving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    @Delegate private final BodylessWithHeaders<ResponseReceivingDeleteMethod> withHeaders

    DeleteMethodBuilder(String host, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders) {
        this.restOperations = restOperations
        params.host = host
        withHeaders =  new BodylessWithHeaders<ResponseReceivingDeleteMethod>(this, params, predefinedHeaders)
    }

    DeleteMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations, NO_PREDEFINED_HEADERS)
    }

    @Override
    ResponseReceivingDeleteMethod onUrl(URI url) {
        params.url = url
        return this
    }
    
    @Override
    ResponseReceivingDeleteMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingDeleteMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    @Override
    UrlParameterizableDeleteMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingDeleteMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingDeleteMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    ResponseEntity aResponseEntity() {
        return new DeleteExecuteForResponseTypeRelated(params, restOperations).exchange()
    }

    @Override
    void ignoringResponse() {
        aResponseEntity()
    }

}
