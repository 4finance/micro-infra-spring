package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.Executable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Interface that provides methods to set headers on HTTP requests
 *
 * @param < T > - interface to return via {@link Executable} when you have finished setting headers
 */
public interface HeadersSetting<T> extends Executable<T> {
    /**
     * List of acceptable {@link MediaType} to be set in the {@link HttpHeaders#ACCEPT} header
     *
     * @param acceptableMediaTypes
     * @return itself
     */
    HeadersSetting<T> accept(List<MediaType> acceptableMediaTypes);

    /**
     * List of acceptable {@link org.springframework.http.MediaType} to be set in the {@link HttpHeaders#ACCEPT} header
     *
     * @param acceptableMediaTypes
     * @return itself
     */
    HeadersSetting<T> accept(MediaType... acceptableMediaTypes);

    /**
     * Sets value for the {@link HttpHeaders#CACHE_CONTROL} header
     *
     * @param cacheControl
     * @return itself
     */
    HeadersSetting<T> cacheControl(String cacheControl);

    /**
     * Sets {@link org.springframework.http.MediaType} for the {@link HttpHeaders#CONTENT_TYPE} header
     *
     * @param mediaType
     * @return itself
     */
    HeadersSetting<T> contentType(MediaType mediaType);

    /**
     * Sets value for the {@link HttpHeaders#CONTENT_TYPE} header
     *
     * @param contentType
     * @return itself
     */
    HeadersSetting<T> contentType(String contentType);

    /**
     * Sets {@link org.springframework.http.MediaType#APPLICATION_JSON_VALUE} for the {@link HttpHeaders#CONTENT_TYPE} header
     *
     * @return itself
     */
    HeadersSetting<T> contentTypeJson();

    /**
     * Sets {@link org.springframework.http.MediaType#APPLICATION_XML_VALUE} for the {@link HttpHeaders#CONTENT_TYPE} header
     *
     * @return itself
     */
    HeadersSetting<T> contentTypeXml();

    /**
     * Sets value for the {@link HttpHeaders#EXPIRES} header
     *
     * @param expires
     * @return itself
     */
     HeadersSetting<T> expires(long expires);

    /**
     * Sets value for the {@link HttpHeaders#LAST_MODIFIED} header
     *
     * @param lastModified
     * @return itself
     */
    HeadersSetting<T> lastModified(long lastModified);

    /**
     * Sets value for the {@link HttpHeaders#LOCATION} header
     *
     * @param location
     * @return itself
     */
    HeadersSetting<T> location(URI location);

    /**
     * Sets value for a header with name
     *
     * @param headerName
     * @param headerValue
     * @return itself
     */
    HeadersSetting<T> header(String headerName, String headerValue);

    /**
     * Sets value for headers from a map (key - header name, value - header value)
     *
     * @param values
     * @return itself
     */
    HeadersSetting<T> headers(Map<String, String> values);

    /**
     * Sets value for headers from {@link HttpHeaders}
     *
     * @param httpHeaders
     * @return itself
     */
    HeadersSetting<T> headers(HttpHeaders httpHeaders);

    /**
     * Sets value for the {@link org.springframework.http.HttpHeaders#AUTHORIZATION} header
     */
    HeadersSetting<T> authentication(String authorization);

    /**
     * Sets basic authentication value for the {@link org.springframework.http.HttpHeaders#AUTHORIZATION} header
     */
    HeadersSetting<T> basicAuthentication(String username, String password);
}
