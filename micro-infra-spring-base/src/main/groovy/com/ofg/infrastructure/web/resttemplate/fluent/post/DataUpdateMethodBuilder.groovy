package com.ofg.infrastructure.web.resttemplate.fluent.post
import com.netflix.hystrix.HystrixCommand
import com.nurkiewicz.asyncretry.RetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.LocationFindingExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.BodyContainingWithHeaders
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersSetting
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
import org.springframework.cloud.sleuth.Trace
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestOperations

import java.util.concurrent.Callable

abstract class DataUpdateMethodBuilder<R, S, T> extends LocationFindingExecutor implements HeadersSetting<T>, HttpMethod<R, S> {

    private final BodyContainingWithHeaders withHeaders
    protected final Trace trace

    DataUpdateMethodBuilder(PredefinedHttpHeaders predefinedHeaders, RestOperations restOperations, RetryExecutor retryExecutor, Trace trace) {
       super(restOperations, retryExecutor, trace)
       this.withHeaders = new BodyContainingWithHeaders(this, params, predefinedHeaders)
       this.trace = trace
    }

    @Override
    HeadersSetting accept(List acceptableMediaTypes) {
        return withHeaders.accept(acceptableMediaTypes)
    }

    HeadersSetting accept(MediaType... acceptableMediaTypes) {
        return withHeaders.accept(acceptableMediaTypes)
    }

    HeadersSetting cacheControl(String cacheControl) {
        return withHeaders.cacheControl(cacheControl)
    }

    HeadersSetting contentType(MediaType mediaType) {
        return withHeaders.contentType(mediaType)
    }

    HeadersSetting contentType(String contentType) {
        return withHeaders.contentType(contentType)
    }

    HeadersSetting contentTypeJson() {
        return withHeaders.contentTypeJson()
    }

    HeadersSetting contentTypeXml() {
        return withHeaders.contentTypeXml()
    }

    HeadersSetting expires(long expires) {
        return withHeaders.expires(expires)
    }

    HeadersSetting lastModified(long lastModified) {
        return withHeaders.lastModified(lastModified)
    }

    HeadersSetting location(URI location) {
        return withHeaders.location(location)
    }

    T andExecuteFor() {
        return withHeaders.andExecuteFor()
    }

    HeadersSetting withHeaders() {
        return withHeaders.withHeaders()
    }


    HeadersSetting header(String headerName, String headerValue) {
        return withHeaders.header(headerName, headerValue)
    }

    @Override
    HeadersSetting headers(HttpHeaders httpHeaders) {
        return withHeaders.headers(httpHeaders)
    }

    @Override
    HeadersSetting authentication(String authorization) {
        return withHeaders.authentication(authorization)
    }

    @Override
    HeadersSetting basicAuthentication(String username, String password) {
        return withHeaders.basicAuthentication(username, password)
    }

    @Override
    HeadersSetting headers(Map values) {
        return withHeaders.headers(values)
    }

    R onUrl(URI url) {
        params.url = url
        return this
    }

    R onUrl(String url) {
        params.url = new URI(url)
        return this
    }

    T httpEntity(HttpEntity httpEntity) {
        params.httpEntity = httpEntity
        return this
    }

    S onUrlFromTemplate(String urlTemplate) {
        params.urlTemplate = urlTemplate
        return this
    }

    @Override
    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<R, S> withCircuitBreaker(HystrixCommand.Setter setter) {
        params.hystrix = setter
        return this
    }

    @Override
    com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod<R, S> withCircuitBreaker(HystrixCommand.Setter setter, Callable hystrixFallback) {
        params.hystrixFallback = setter
        return withCircuitBreaker(setter)
    }

    R withVariables(Object... urlVariables) {
        params.urlVariablesArray = urlVariables
        return this
    }

    R withVariables(Map<String, ?> urlVariables) {
        params.urlVariablesMap = urlVariables
        return this
    }

}
