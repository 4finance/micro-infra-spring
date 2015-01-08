package com.ofg.infrastructure.base.dsl

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType


/**
 * A class that contains static helper methods that map HTTP responses with different
 * statuses, content types and contents
 */
@CompileStatic
class StubbedHttpResponseBuilder {

    static ResponseDefinitionBuilder xmlResponse(HttpStatus status, String responseBodyFileName) {
        return createResponse(status, responseBodyFileName, MediaType.APPLICATION_XML_VALUE)
    }

    static ResponseDefinitionBuilder xmlFileResponse(HttpStatus status, String responseBodyFileName) {
        return createFileResponse(status, responseBodyFileName, MediaType.APPLICATION_XML_VALUE)
    }

    static ResponseDefinitionBuilder jsonResponse(HttpStatus status, String responseBody) {
        return createResponse(status, responseBody, MediaType.APPLICATION_JSON_VALUE)
    }

    static ResponseDefinitionBuilder jsonResponse(String responseBody) {
        return createResponse(HttpStatus.OK, responseBody, MediaType.APPLICATION_JSON_VALUE)
    }

    static ResponseDefinitionBuilder jsonFileResponse(String responseBodyFileName) {
        return jsonFileResponse(HttpStatus.OK, responseBodyFileName)
    }
    
    static ResponseDefinitionBuilder jsonFileResponse(HttpStatus status, String responseBodyFileName) {
        return createFileResponse(status, responseBodyFileName, MediaType.APPLICATION_JSON_VALUE)
    }

    static ResponseDefinitionBuilder notFoundResponse() {
        return WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())
    }

    private static ResponseDefinitionBuilder createResponse(HttpStatus status, String responseBody, String contentType) {
        return WireMock.aResponse()
                .withStatus(status.value())
                .withHeader('Content-Type', contentType)
                .withBody(responseBody)
    }   
    
    private static ResponseDefinitionBuilder createFileResponse(HttpStatus status, String responseBodyFileName, String contentType) {
        return WireMock.aResponse()
                .withStatus(status.value())
                .withHeader('Content-Type', contentType)
                .withBodyFile("/$responseBodyFileName")
    }    
}
