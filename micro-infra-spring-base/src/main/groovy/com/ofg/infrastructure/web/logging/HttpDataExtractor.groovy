package com.ofg.infrastructure.web.logging

import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils

@CompileStatic
@TypeChecked
class HttpDataExtractor {

    static Map<String, String> extractHeaders(HttpServletRequestLoggingWrapper httpServletRequest){
        Map<String, String> headers = [:]
        httpServletRequest.headerNames.toList().each {String it -> headers.put(it, httpServletRequest.getHeader(it))}
        return headers
    }

    static Map<String, String> extractHeaders(HttpServletResponseLoggingWrapper httpServletResponse){
        Map<String, String> headers = [:]
        httpServletResponse.headerNames.toList().each {String it -> headers.put(it, httpServletResponse.getHeader(it))}
        return headers
    }

    static Map<String, String> extractHeaders(HttpRequest request){
        return request.headers.toSingleValueMap()
    }

    static Map<String, String> extractHeaders(ClientHttpResponse response){
        return response.headers.toSingleValueMap()
    }

    static String extractUrl(HttpRequest request){
        return request.URI.toString()
    }

    static String extractUrl(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.requestURI
    }

    static String extractPath(HttpRequest request){
        return request.URI.path
    }

    static String extractPath(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.requestURI
    }

    static String extractMethod(HttpRequest request){
        return request.method.name()
    }

    static String extractMethod(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.method
    }

    static int extractStatus(ClientHttpResponse response){
        return response.statusCode.value()
    }

    static int extractStatus(HttpServletResponseLoggingWrapper response){
        return response.status
    }

    static String extractContent(HttpServletResponseLoggingWrapper response){
        return new String(response.bytes)
    }

    static String extractContent(HttpServletRequestLoggingWrapper request){
        return new String(request.bytes)
    }

    static String extractContent(ClientHttpResponse response){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamUtils.copy(response.body, output)
        return new String(output.toByteArray())
    }

}
