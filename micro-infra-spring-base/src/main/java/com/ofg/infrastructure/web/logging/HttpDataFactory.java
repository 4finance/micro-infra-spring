package com.ofg.infrastructure.web.logging;

import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper;
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper;
import feign.Request;
import feign.Response;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractContent;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractHeaders;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractMethod;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractPath;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractResponseCode;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractStatus;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractUrl;

class HttpDataFactory {

    static HttpData createHttpData(HttpRequest request, byte[] content){
        return new HttpData(extractHeaders(request),
                            extractUrl(request),
                            extractPath(request),
                            extractMethod(request),
                            new String(content));
    }

    static HttpData createHttpData(HttpServletRequestLoggingWrapper request){
        return new HttpData(extractHeaders(request),
                            extractUrl(request),
                            extractPath(request),
                            extractMethod(request),
                            extractContent(request));
    }
    
    static HttpData createHttpData(Request request) {
        return new HttpData(extractHeaders(request), 
                            extractUrl(request), 
                            extractPath(request), 
                            extractMethod(request), 
                            extractContent(request));
    }

    static HttpData createHttpData(HttpServletResponseLoggingWrapper response){
        return new HttpData(extractHeaders(response),
                            extractStatus(response),
                            extractContent(response));
    }

    static HttpData createHttpData(ClientHttpResponse response){
        return new HttpData(response.getHeaders().toSingleValueMap(),
                            extractResponseCode(response),
                            extractContent(response));
    }
    
    static HttpData createHttpData(Response response) {
        return new HttpData(extractHeaders(response), extractStatus(response), extractContent(response));
    }
}
