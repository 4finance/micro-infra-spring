package com.ofg.infrastructure.web.resttemplate.fluent.head
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodylessWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

@TypeChecked
class HeadMethodBuilder implements HeadMethod, UrlParameterizableHeadMethod, ResponseReceivingHeadMethod, HeadersHaving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestOperations restOperations
    @Delegate private final BodylessWithHeaders<ResponseReceivingHeadMethod> withHeaders

    HeadMethodBuilder(String host, RestOperations restOperations) {
        this.restOperations = restOperations
        params.host = host
        withHeaders =  new BodylessWithHeaders<ResponseReceivingHeadMethod>(this, params)
    }

    HeadMethodBuilder(RestOperations restOperations) {
        this(EMPTY_HOST, restOperations)
    }

    @Override
    ResponseReceivingHeadMethod onUrl(URI url) {
        params.url = url
        return this
    }

    @Override
    ResponseReceivingHeadMethod onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    @Override
    ResponseReceivingHeadMethod httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    @Override
    UrlParameterizableHeadMethod onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    ResponseReceivingHeadMethod withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    @Override
    ResponseReceivingHeadMethod withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

    @Override
    ResponseEntity aResponseEntity() {
        return new HeadExecuteForResponseTypeRelated(params, restOperations).exchange()
    }

    @Override
    HttpHeaders httpHeaders() {
        return new HeadExecuteForResponseTypeRelated(params, restOperations).exchange()?.headers
    }

    @Override
    void ignoringResponse() {
        aResponseEntity()
    }

}
