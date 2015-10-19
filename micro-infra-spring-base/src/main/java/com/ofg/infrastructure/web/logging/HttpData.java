package com.ofg.infrastructure.web.logging;

import java.util.Map;

class HttpData {

    private final Map<String, String> headers;
    private final String httpMethod ;
    private final String url ;
    private final String path ;
    private final int httpStatus;
    private final String content;
    private Map<String, String> processedHeaders;
    private String processedContent;

    HttpData(Map<String, String> headers, String url, String path, String httpMethod,  String content) {
        this.headers = headers;
        this.httpMethod = httpMethod;
        this.url = url;
        this.path = path;
        this.httpStatus = 0;
        this.content = content;
    }

    HttpData(Map<String, String> headers, int httpStatus,  String content) {
        this.headers = headers;
        this.httpStatus = httpStatus;
        this.content = content;
        this.httpMethod = "";
        this.url = "";
        this.path = "";
    }

    Map<String, String> getProcessedHeaders() {
        return processedHeaders;
    }

    void setProcessedHeaders(Map<String, String> processedHeaders) {
        this.processedHeaders = processedHeaders;
    }

    String getProcessedContent() {
        return processedContent;
    }

    void setProcessedContent(String processedContent) {
        this.processedContent = processedContent;
    }

    Map<String, String> getHeaders() {
        return headers;
    }

    String getHttpMethod() {
        return httpMethod;
    }

    String getUrl() {
        return url;
    }

    String getPath() {
        return path;
    }

    int getHttpStatus() {
        return httpStatus;
    }

    String getContent() {
        return content;
    }
}
