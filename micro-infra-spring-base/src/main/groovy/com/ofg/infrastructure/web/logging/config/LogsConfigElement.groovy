package com.ofg.infrastructure.web.logging.config

class LogsConfigElement {

    String skipAll = 'false'
    String method
    String urlPattern
    List<String> filteredReqHeaders = new LinkedList<>()
    List<String> filteredResHeaders = new LinkedList<>()
    List<String> filteredReqFields = new LinkedList<>()
    List<String> filteredResFields = new LinkedList<>()

    boolean isRequestHeaderAllowed(String headerName){
        return filteredReqHeaders.findAll {String it -> it.toUpperCase().equals(headerName?.toUpperCase())}.size() == 0
    }

    boolean isResponseHeaderAllowed(String headerName){
        return filteredResHeaders.findAll {String it -> it.toUpperCase().equals(headerName?.toUpperCase())}.size() == 0
    }

    Map<String, String>  getRequestHeadersToLogging(Map<String, String> httpHeaders) {
        return httpHeaders.findAll { Map.Entry<String, String> it -> this.isRequestHeaderAllowed(it.key) }
    }

    Map<String, String>  getResponseHeadersToLogging(Map<String, String> httpHeaders) {
        return httpHeaders.findAll { Map.Entry<String, String> it -> this.isResponseHeaderAllowed(it.key) }
    }
}
