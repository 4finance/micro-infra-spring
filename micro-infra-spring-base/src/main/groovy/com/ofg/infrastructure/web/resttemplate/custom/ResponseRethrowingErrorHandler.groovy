package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler

/**
 * RestTemplate {@link ResponseErrorHandler} that on statuses equal to 4xx or 5xx
 * logs an error response body, status code and then rethrows exceptions {@link ResponseException}
 *
 * @see org.springframework.web.client.RestTemplate
 * @see ResponseErrorHandler
 */
@Slf4j
@CompileStatic
class ResponseRethrowingErrorHandler implements ResponseErrorHandler {

    @Override
    boolean hasError(ClientHttpResponse response) throws IOException {
        return HttpStatusVerifier.isError(response.statusCode)
    }

    @Override
    void handleError(ClientHttpResponse response) throws IOException {
        String responseBody = getLoggedErrorResponseBody(response)
        throw new ResponseException(response.statusCode, responseBody, response.headers)
    }

    protected String getLoggedErrorResponseBody(ClientHttpResponse response) {
        String responseBody = getResponseBody(response)
        log.error("Response error: status code [$response.statusCode], headers [$response.headers], body [$responseBody]")
        return responseBody
    }

    private String getResponseBody(ClientHttpResponse response) {
        try {
            return response.body?.text
        } catch (IOException e) {
            log.debug("Exception while loading body from error response", e)
            return null
        }
    }
}
