package com.ofg.infrastructure.web.resttemplate.custom

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class ResponseRethrowingErrorHandlerSpec extends Specification {

    def 'should throw ResponseException with status code, response body and headers'() {
        given:
            ResponseRethrowingErrorHandler handler = new ResponseRethrowingErrorHandler()
            ClientHttpResponse response = errorHttpResponse()
        when:
            handler.handleError(response)
        then:
            ResponseException ex = thrown()
            ex.httpStatus == HttpStatus.BAD_REQUEST
            ex.body == 'error response body'
            ex.headers == ["Content-Length":['123'], "Content-Type":[MediaType.APPLICATION_JSON_VALUE]]
    }

    private ClientHttpResponse errorHttpResponse() {
        ClientHttpResponse response = Mock()
        response.statusCode >> HttpStatus.BAD_REQUEST
        response.body >> inputStream()
        response.headers >> headers()
        return response
    }

    HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentLength(123L)
        headers.setContentType(MediaType.APPLICATION_JSON)
        return headers
    }

    private InputStream inputStream() {
        return new ByteArrayInputStream('error response body'.getBytes(StandardCharsets.UTF_8));
    }

}
