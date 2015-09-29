package com.ofg.infrastructure.web.logging

import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper
import groovy.transform.CompileStatic
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils

@CompileStatic
class HttpDataFactory {

    static HttpData createHttpData(HttpRequest request, byte[] content){
        return new HttpData(headers : extractHeaders(request),
                            url : request.URI.toString(),
                            path : request.URI.path,
                            httpMethod : request.method.name(),
                            content: new String(content))
    }

    static HttpData createHttpData(HttpServletRequestLoggingWrapper request){
        return new HttpData(headers : extractHeaders(request),
                            url : request.requestURI,
                            path : request.requestURI,
                            httpMethod : request.method,
                            content: new String(request.bytes))
    }

    static HttpData createHttpData(HttpServletResponseLoggingWrapper response){
        return new HttpData(headers : extractHeaders(response),
                            httpStatus : response.status,
                            content: new String(response.bytes))
    }

    static HttpData createHttpData(ClientHttpResponse response){
        return new HttpData(headers : response.headers.toSingleValueMap(),
                            httpStatus : response.statusCode.value(),
                            content: extractContent(response))
    }

    private static Map<String, String> extractHeaders(HttpRequest request){
        return request.headers.toSingleValueMap()
    }

    private static Map<String, String> extractHeaders(HttpServletRequestLoggingWrapper httpServletRequest){
        Map<String, String> headers = [:]
        httpServletRequest.headerNames.toList().each {String it -> headers.put(it, httpServletRequest.getHeader(it))}
        return headers
    }

    private static Map<String, String> extractHeaders(HttpServletResponseLoggingWrapper httpServletResponse){
        Map<String, String> headers = [:]
        httpServletResponse.headerNames.toList().each {String it -> headers.put(it, httpServletResponse.getHeader(it))}
        return headers
    }


    private static String extractContent(ClientHttpResponse response){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamUtils.copy(response.body, output)
        return new String(output.toByteArray())
    }
}
