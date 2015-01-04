package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod
import com.ofg.infrastructure.web.resttemplate.fluent.common.request.ParametrizedUrlHavingWith
import org.springframework.http.HttpEntity

trait MethodParamsApplier<M, EM, PM> implements HttpMethod<M, PM>, HttpEntitySending<EM>, ParametrizedUrlHavingWith<M> {

    M onUrl(URI url) {
        params.url = url
        this
    }

    M onUrl(String url) {
        params.url = new URI(url)
        this
    }

    M onUrl(GString url) {
        final String urlTemplate = gStringToParameterizedUrlTemplate(url)
        params.urlTemplate = urlTemplate
        params.urlVariablesArray = url.values
        this
    }

    private String gStringToParameterizedUrlTemplate(GString url) {
        String urlTemplate = url.strings.head()
        url.strings.tail().eachWithIndex { String entry, int index ->
            urlTemplate += "{p$index}$entry"
        }
        return urlTemplate
    }

    EM httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        this
    }

    PM onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        this
    }

    M withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        this
    }

    M withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        this
    }
}
