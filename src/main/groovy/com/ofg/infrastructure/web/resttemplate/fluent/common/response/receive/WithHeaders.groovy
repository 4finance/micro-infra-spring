package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import groovy.transform.TypeChecked
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_XML

@TypeChecked
class WithHeaders<T> implements HeadersSetting<T>, HeadersHaving<T> {

    private static final String CONTENT_TYPE_HEADER_NAME = 'Content-Type'
    
    private final HttpHeaders httpHeaders = new HttpHeaders()
    private final Map params
    private final T parent

    WithHeaders(T parent, Map<String, String> params) {
        this.params = params
        this.parent = parent
    }

    @Override
    WithHeaders accept(List<MediaType> acceptableMediaTypes) {
        httpHeaders.setAccept(acceptableMediaTypes)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders accept(MediaType... acceptableMediaTypes) {
        return accept(Arrays.asList(acceptableMediaTypes))
    }

    @Override
    WithHeaders cacheControl(String cacheControl) {
        httpHeaders.setCacheControl(cacheControl)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders contentType(MediaType mediaType) {
        httpHeaders.setContentType(mediaType)
        updateHeaderParams()
        return this
    }

    @Override
    HeadersSetting<ResponseReceiving> contentType(String contentType) {
        httpHeaders.add(CONTENT_TYPE_HEADER_NAME, contentType)
        updateHeaderParams()
        return this
    }

    @Override
    HeadersSetting<T> contentTypeJson() {
        httpHeaders.setContentType(APPLICATION_JSON)
        updateHeaderParams()
        return this
    }

    @Override
    HeadersSetting<T> contentTypeXml() {
        httpHeaders.setContentType(APPLICATION_XML)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders expires(long expires) {
        httpHeaders.setExpires(expires)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders lastModified(long lastModified) {
        httpHeaders.setLastModified(lastModified)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders location(URI location) {
        httpHeaders.setLocation(location)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders header(String headerName, String headerValue) {
        httpHeaders.add(headerName, headerValue)
        updateHeaderParams()
        return this
    }

    @Override
    WithHeaders headers(Map<String, String> values) {
        httpHeaders.setAll(values)
        updateHeaderParams()
        return this
    }

    @Override
    HeadersSetting<T> headers(HttpHeaders httpHeaders) {
        params.headers = httpHeaders
        return this
    }

    private void updateHeaderParams() {
        params.headers = httpHeaders
    }

    @Override
    T andExecuteFor() {
        return parent
    }

    @Override
    HeadersSetting<ResponseReceiving> withHeaders() {
        return this
    }
}
