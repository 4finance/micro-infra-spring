package com.ofg.infrastructure.web.logging;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper;
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper;
import feign.Request;
import feign.Response;

class HttpDataExtractor {

    private static final Logger log = LoggerFactory.getLogger(HttpDataExtractor.class);

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
    
    static Map<String, String> extractHeaders(Request request) {
        return request.headers().entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey(),
                e -> StringUtils.collectionToCommaDelimitedString(e.getValue())
        ));
    }
    
    static Map<String, String> extractHeaders(Response response) {
        return response.headers().entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey(),
                e -> StringUtils.collectionToCommaDelimitedString(e.getValue())
        ));
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
    
    static String extractUrl(Request request) {
        return request.url();
    }

    static String extractPath(HttpRequest request){
        return request.getURI().getPath();
    }

    static String extractPath(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.getRequestURI();
    }
    
    static String extractPath(Request request) {
        return URI.create(request.url()).getPath();
    }

    static String extractMethod(HttpRequest request){
        return request.getMethod().name();
    }

    static String extractMethod(HttpServletRequestLoggingWrapper httpServletRequest){
        return httpServletRequest.getMethod();
    }
    
    static String extractMethod(Request request) {
        return request.method();
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

    static int extractStatus(Response response) {
        return response.status();
    }
    
    static String extractContent(HttpServletResponseLoggingWrapper response){
        return new String(response.getBytes());
    }

    static String extractContent(HttpServletRequestLoggingWrapper request){
        return new String(request.getBytes());
    }

    static String extractContent(Request request) {
        return request.body() == null ? "" : new String(request.body());
    }
    
    static String extractContent(ClientHttpResponse response) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            StreamUtils.copy(response.getBody(), output);
        } catch (FileNotFoundException ex) {
            log.debug("ExtractContent error:", ex);
        } catch (Exception ex) {
            log.error("ExtractContent error:", ex);
        }
        return new String(output.toByteArray());
    }
    
    static String extractContent(Response response) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try{
            StreamUtils.copy(response.body().asInputStream(), output);
        } catch (FileNotFoundException ex) {
            log.debug("ExtractContent error:", ex);
        } catch (Exception ex) {
            log.error("ExtractContent error:", ex);
        }
        return new String(output.toByteArray());
    }

}
