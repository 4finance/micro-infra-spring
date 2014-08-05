package com.ofg.infrastructure.base.dsl
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import groovy.transform.TypeChecked
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
//TODO: this needs a usage example (preferably as tests)
@TypeChecked
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
        return com.github.tomakehurst.wiremock.client.WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())
    }

    private static ResponseDefinitionBuilder createResponse(HttpStatus status, String responseBody, String contentType) {
        return com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                .withStatus(status.value())
                .withHeader('Content-Type', contentType)
                .withBody(responseBody)
    }   
    
    private static ResponseDefinitionBuilder createFileResponse(HttpStatus status, String responseBodyFileName, String contentType) {
        return com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                .withStatus(status.value())
                .withHeader('Content-Type', contentType)
                .withBodyFile("/$responseBodyFileName")
    }    
}
