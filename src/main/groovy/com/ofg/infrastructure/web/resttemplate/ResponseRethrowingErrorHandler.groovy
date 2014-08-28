package com.ofg.infrastructure.web.resttemplate

import groovy.transform.TypeChecked
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
@TypeChecked
@Slf4j
class ResponseRethrowingErrorHandler implements ResponseErrorHandler {

    @Override
    boolean hasError(ClientHttpResponse response) throws IOException {
        return HttpStatusVerifier.isError(response.getStatusCode())
    }

    @Override
    void handleError(ClientHttpResponse response) throws IOException {
        String responseBody = getLoggedErrorResponseBody(response)
        throw new ResponseException(String.format("Body: [%s]", responseBody))
    }

    protected String getLoggedErrorResponseBody(ClientHttpResponse response) {
        String responseBody = response.body?.text
        log.error("Response error: status code [$response.statusCode] body [$responseBody]")
        return responseBody
    }

}
