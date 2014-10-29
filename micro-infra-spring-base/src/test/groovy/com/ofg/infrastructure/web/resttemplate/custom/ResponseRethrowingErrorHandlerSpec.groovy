package com.ofg.infrastructure.web.resttemplate.custom

import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class ResponseRethrowingErrorHandlerSpec extends Specification {

    def 'should throw ResponseException with status code and response body'() {
        given:
            ResponseRethrowingErrorHandler handler = new ResponseRethrowingErrorHandler()
            ClientHttpResponse response = errorHttpResponse()
        when:
            handler.handleError(response)
        then:
            ResponseException ex = thrown()
            ex.httpStatus == HttpStatus.BAD_REQUEST
            ex.message == 'Status code [400], Body: error response body'
    }

    private ClientHttpResponse errorHttpResponse() {
        ClientHttpResponse response = Mock()
        response.statusCode >> HttpStatus.BAD_REQUEST
        response.body >> inputStream()
        return response
    }

    private InputStream inputStream() {
        return new ByteArrayInputStream('error response body'.getBytes(StandardCharsets.UTF_8));
    }

}
