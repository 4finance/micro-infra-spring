package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.netflix.hystrix.HystrixCommand
import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod
import com.ofg.infrastructure.web.resttemplate.fluent.common.request.ParametrizedUrlHavingWith
import org.apache.commons.lang.StringUtils
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
        return onUrlFromTemplate(urlTemplate)
                .withVariables(url.values)
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
        if(templateStartsWithPlaceholder()) {
            replaceFirstPlaceholderWithValue()
        }
        this
    }

    private def replaceFirstPlaceholderWithValue() {
        final String template = params.urlTemplate
        final String skippedFirstPlaceholder = StringUtils.substringAfter(template, '}')
        final Object[] variables = params.urlVariablesArray
        params.urlTemplate = variables.head() + skippedFirstPlaceholder
        params.urlVariablesArray = variables.tail()
    }

    private boolean templateStartsWithPlaceholder() {
        return params.urlTemplate.startsWith('{')
    }

    M withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        this
    }

    HttpMethod<M, PM> withCircuitBreaker(HystrixCommand.Setter setter) {
        params.hystrix = setter
        return this
    }
}
