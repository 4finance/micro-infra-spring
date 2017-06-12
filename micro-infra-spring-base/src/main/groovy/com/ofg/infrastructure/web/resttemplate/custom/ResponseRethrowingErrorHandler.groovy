package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler

import static com.ofg.infrastructure.web.resttemplate.custom.ResponseRethrowingErrorHandler.ErrorMessageLoggingLevel.ERROR
import static com.ofg.infrastructure.web.resttemplate.custom.ResponseRethrowingErrorHandler.ErrorMessageLoggingLevel.WARN

/**
 * RestTemplate {@link ResponseErrorHandler} that on statuses equal to 4xx or 5xx
 * logs an error response body, status code (if configured) and then rethrows exceptions {@link ResponseException}
 *
 * @see org.springframework.web.client.RestTemplate
 * @see ResponseErrorHandler
 */
@Slf4j
@CompileStatic
class ResponseRethrowingErrorHandler implements ResponseErrorHandler {

    static final enum ErrorMessageLoggingLevel {
        NONE, WARN, ERROR
    }

    private final ErrorMessageLoggingLevel errorMessageLoggingLevel

    ResponseRethrowingErrorHandler() {
        this(Optional.empty())
    }

    ResponseRethrowingErrorHandler(Optional<String> errorMessageLoggingLevel) {
        this.errorMessageLoggingLevel = safelyParseErrorMessageLoggingLevel(errorMessageLoggingLevel)
    }

    private static ErrorMessageLoggingLevel safelyParseErrorMessageLoggingLevel(Optional<String> errorMessageLoggingLevel) {
        try {
            return errorMessageLoggingLevel
                    .map({ ErrorMessageLoggingLevel.valueOf(it) })
                    .orElse(ERROR)
        } catch (Exception ignored) {
            log.error("Incorrect value of errorMessageLoggingLevel: $errorMessageLoggingLevel, setting to ERROR")
            return ERROR
        }
    }

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

        switch (errorMessageLoggingLevel) {
            case ERROR:
                log.error("Response error: status code [$response.statusCode], headers [$response.headers], body [$responseBody]")
                break
            case WARN:
                log.warn("Response error: status code [$response.statusCode], headers [$response.headers], body [$responseBody]")
                break
        }
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
