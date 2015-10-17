package com.ofg.infrastructure.web.logging;

import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper;
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

class HttpDataExtractor {

    static Map<String, String> extractHeaders(HttpServletRequestLoggingWrapper httpServletRequest){
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header, httpServletRequest.getHeader(header));
        }
        return headers;
    }

    static Map<String, String> extractHeaders(HttpServletResponseLoggingWrapper httpServletResponse){
        Map<String, String> headers =  new HashMap<>();
        for(String headerName : httpServletResponse.getHeaderNames()) {
            headers.put(headerName, httpServletResponse.getHeader(headerName));
        }
        return headers;
    }

    static Map<String, String> extractHeaders(HttpRequest request){
        return request.getHeaders().toSingleValueMap();
    }

    static Map<String, String> extractHeaders(ClientHttpResponse response){
        return response.getHeaders().toSingleValueMap();
    }

    static String extractUrl(HttpRequest request){
        return request.getURI().toString();
    }

    static String extractUrl(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.getRequestURI();
    }

    static String extractPath(HttpRequest request){
        return request.getURI().getPath();
    }

    static String extractPath(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.getRequestURI();
    }

    static String extractMethod(HttpRequest request){
        return request.getMethod().name();
    }

    static String extractMethod(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.getMethod();
    }

    static int extractResponseCode(ClientHttpResponse response){
        try {
            return response.getStatusCode().value();
        } catch (Exception ex){
            throw new IllegalStateException("Error extractResponseCode", ex);
        }
    }

    static int extractStatus(HttpServletResponseLoggingWrapper response){
        return response.getStatus();
    }

    static String extractContent(HttpServletResponseLoggingWrapper response){
        return new String(response.getBytes());
    }

    static String extractContent(HttpServletRequestLoggingWrapper request){
        return new String(request.getBytes());
    }

    static String extractContent(ClientHttpResponse response){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try{
            StreamUtils.copy(response.getBody(), output);
        } catch (Exception ex) {
            throw new IllegalStateException("Error extractResponseCode", ex);
        }
        return new String(output.toByteArray());
    }

}
