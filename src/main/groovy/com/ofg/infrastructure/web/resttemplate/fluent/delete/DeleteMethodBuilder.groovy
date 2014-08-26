package com.ofg.infrastructure.web.resttemplate.fluent.delete
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodylessWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersHaving
import groovy.transform.TypeChecked
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@TypeChecked
class DeleteMethodBuilder implements DeleteMethod, UrlParameterizableDeleteMethod, ResponseReceivingDeleteMethod, HeadersHaving {

    public static final String EMPTY_HOST = ''

    private final Map params = [:]
    private final RestTemplate restTemplate
    @Delegate private final BodylessWithHeaders<ResponseReceivingDeleteMethod> withHeaders

    DeleteMethodBuilder(String host, RestTemplate restTemplate) {
        this.restTemplate = restTemplate
        params.host = host
        withHeaders =  new BodylessWithHeaders<ResponseReceivingDeleteMethod>(this, params)
    }

    DeleteMethodBuilder(RestTemplate restTemplate) {
        this(EMPTY_HOST, restTemplate)
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
        return new DeleteExecuteForResponseTypeRelated(params, restTemplate).exchange()
    }

    @Override
    void ignoringResponse() {
        aResponseEntity()
    }

}
