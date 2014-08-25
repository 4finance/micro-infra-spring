package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

interface HeadersSetting<T> extends Executable<T> {

    HeadersSetting<T> accept(List<MediaType> acceptableMediaTypes)

    HeadersSetting<T> cacheControl(String cacheControl)

    HeadersSetting<T> contentType(MediaType mediaType)
    
    HeadersSetting<T> contentType(String contentType)

    HeadersSetting<T> expires(long expires)

    HeadersSetting<T> lastModified(long lastModified)

    HeadersSetting<T> location(URI location)

    HeadersSetting<T> header(String headerName, String headerValue)

    HeadersSetting<T> headers(Map<String, String> values)
    
    HeadersSetting<T> headers(HttpHeaders httpHeaders)

}